<?php
/*
 * Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 */

class PDFT extends FPDF {

  //data node types
  const TYPE_QUESTIONS       = 'question';
  const TYPE_QUESTIONS_TABLE = 'question-table';
  const TYPE_COMPLEX_TABLE   = 'complex-table';
  
  private $xml, $data, $count;

  public $javascript;
  public $n_js;
  
  public function setCount($c) {
    $this->count = $c;
  }

//Simple table
  public function BasicTable() {
    $max = 0;
    $ss = '  :';

    foreach($this->data as $row){

      $is_array = is_array($row['answer']) && !empty($row['answer']);
      $is_line =  !empty($row['answer']) &&  !is_array($row['answer']);

      if($is_line || $is_array){
        $answer = '';  
        $question = split_string(cacure_pdf_prepare_string($row['text']), 70);
        //get question and answer
        if($is_line){
          $answer = (isset($row['answer_attr']['text']) && !empty($row['answer_attr']['text'])) ? $row['answer_attr']['text'] : $row['answer'];
          $count = count($question);

          $first_line = array_shift($question);
            
          //render

          if($count == 1){
            $this->Cell(92, 6, $first_line . $ss);
            $this->Cell(130, 6, $answer);
            $this->Ln();
            continue;
          }else{
            $this->Cell(92, 6, $first_line);
            $this->Ln();
          }
          $ss = '';
          if(!empty($question)){


            for($i=0; $i < count($question); $i++){

              $last = false;
              $ss = '';

              if(!isset($question[$i+1]) || empty($question[$i+1])){
                $last = true;
                $ss = '  :';
              }

              $this->Cell(92, 6, $question[$i] . $ss);
              if($last) $this->Cell(130, 6, $answer);
              $this->Ln();
            }
          }

        }else{
          $answer = $row['answer'];
        }
      }
    }
  }

  public function aboutTable($data, $alternate_info) {
    $max = 0;
    while (list($key) = each($data)) {
      $current = strlen($key);
      if ($current > $max) {
        $max = $current;
      }
    }

    $max += 5;
    foreach ($data as $title => $val) {
      $this->SetFont('Arial', 'B', 6);
      $this->Cell($max + 2, 6, $title . '  :');
      $this->SetFont('Arial', '', 6);
      $this->Cell(100, 6, $val);
      $this->Ln();
    }

    $this->SetY(30);

    foreach($alternate_info as $title => $val){
      $this->SetX(100);
      $this->SetFont('Arial', 'B', 6);
      $this->Cell($max + 2, 6, $title . '  :');
      $this->SetFont('Arial', '', 6);
      $this->Cell(100, 6, $val);
      $this->Ln();
    }
  }

  public function loadData($xml) {
    $this->xml = $xml;
    $this->data = $this->xml['form']['question'];
  }

  public function Header() {
    global $base_path;
    $this->SetY(0);
    if($this->PageNo() === 1){
      //$this->Image(drupal_get_path('theme', 'acquia_marina') . '/images/pdf-title.jpeg', 8.5, 0, 40, 17, 'JPEG', 'https://' . $_SERVER['SERVER_NAME'] . '/');
	    $this->Image($base_path.'/sites/default/files/logo.jpg', 8.5, 1, 15, 16, 'PNG', 'https://' . $_SERVER['SERVER_NAME'] . '/');
    }
    $this->Cell(70);
    $this->SetFont('Arial', '', 14);
    $this->Cell(26, 10, 'Submitted Questionnaire', 0, 0);
    $this->Cell(90);
    $this->Cell(0, 10, 'Page ' . $this->PageNo() . '/' . $this->count, 0, 0, 'C');
    $this->Ln(20);
  }

  public function setFormTitle() {
    $this->SetFont('Arial', 'B', 14);
    $this->MultiCell(0, 6, $this->xml['form_attr']['name']);
    //$this->SetY(-15);
    $this->SetFont('Arial', 'I', 8);
    $this->Ln(4);
  }

  public function setSimpleTitle($title = 'undefined', $title_alt = 'underfined') {
    $this->SetFont('Arial', 'B', 14);
    //$this->SetFont('Arial', 'I', 8);
    if($title_alt == 'underfined'){
      $this->Cell(0, 6, $title);

    }else{
      $this->Cell(0, 6, $title);
      $this->SetX(100);
      $this->Cell(10, 6, $title_alt);
    }
    $this->Ln(10);
  }

  function Footer() {
    $this->SetY(-15);
    $this->SetFont('Arial', '', 14);
    //$this->Cell(30,10,'http://how.healthcit.com',0,0);
    $this->Cell(170);
    $this->Cell(0, 10, date("Y/m/d"), 'C');
  }

  function IncludeJS($script) {
    $this->javascript = $script;
  }

  function _putjavascript() {
    $this->_newobj();
    $this->n_js = $this->n;
    $this->_out('<<');
    $this->_out('/Names [(EmbeddedJS) ' . ($this->n + 1) . ' 0 R]');
    $this->_out('>>');
    $this->_out('endobj');
    $this->_newobj();
    $this->_out('<<');
    $this->_out('/S /JavaScript');
    $this->_out('/JS ' . $this->_textstring($this->javascript));
    $this->_out('>>');
    $this->_out('endobj');
  }

  function _putresources() {
    parent::_putresources();
    if (!empty($this->javascript)) {
      $this->_putjavascript();
    }
  }

  function _putcatalog() {
    parent::_putcatalog();
    if (!empty($this->javascript)) {
      $this->_out('/Names <</JavaScript ' . ($this->n_js) . ' 0 R>>');
    }
  }

  function AutoPrint($dialog = false) {
    //Open the print dialog or start printing immediately on the standard printer
    $param = ($dialog ? 'true' : 'false');
   // $script = "print($param);";
    $script = $this->script;
    $this->IncludeJS($script);
  }

  function AutoPrintToPrinter($server, $printer, $dialog = false) {
    //Print on a shared printer (requires at least Acrobat 6)
    $script = "var pp = getPrintParams();";
    if ($dialog)
      $script .= "pp.interactive = pp.constants.interactionLevel.full;";
    else
      $script .= "pp.interactive = pp.constants.interactionLevel.automatic;";
    $script .= "pp.printerName = '\\\\\\\\" . $server . "\\\\" . $printer . "';";
    $script .= "print(pp);";
    $this->IncludeJS($script);
  }

   /**
    * Shows data for page
    */
    public function showData()
    {
        if (empty($this->xml['form'])) return;

        $data = $this->xml['form'];
        
        if (!empty($data[self::TYPE_QUESTIONS]))
        {
            $this->showQuestions($data[self::TYPE_QUESTIONS], 
                $this->getAttributes($data, self::TYPE_QUESTIONS));
        }
        if (!empty($data[self::TYPE_QUESTIONS_TABLE]))
        {
            $this->showQuestionsTable($data[self::TYPE_QUESTIONS_TABLE], 
                $this->getAttributes($data, self::TYPE_QUESTIONS_TABLE));
        }
        if (!empty($data[self::TYPE_COMPLEX_TABLE]))
        {
            $this->showComplexTable($data[self::TYPE_COMPLEX_TABLE],
                $this->getAttributes($data, self::TYPE_COMPLEX_TABLE));
        }
    }
    
    /**
     * Shows questions node
     * @param array $data
     * @param array $attributes
     */
    protected function showQuestions($data, $attributes)
    {
        $this->normalizeData($data);
        
        foreach ($data as $index => $question)
        { 
            if (!$this->checkIndex($index)) continue;

            $this->showQuestionBlock($question, $this->getAttributes($data, $index));
        }
    }
    
    /**
     * Shows questions-table node
     * @param array $data
     * @param array $attributes
     */
    protected function showQuestionsTable($data, $attributes)
    {
        $this->normalizeData($data);
        
        foreach ($data as $index => $questionsBlock)
        { 
            if (!$this->checkIndex($index)) continue;
            
            $this->showQuestionsTitle($questionsBlock);
        
            if (!empty($questionsBlock[self::TYPE_QUESTIONS]))
            {
                $this->showQuestions($questionsBlock[self::TYPE_QUESTIONS], 
                    $this->getAttributes($questionsBlock, self::TYPE_QUESTIONS));
            }
        }
    }
    
    /**
     * Shows complex-table node
     * @param array $data
     * @param array $attributes
     */
    protected function showComplexTable($data, $attributes)
    {
        $this->normalizeData($data);
        
        foreach ($data as $index => $table)
        {
            if (!$this->checkIndex($index)) continue;
            
            $this->showQuestionsTitle($table);
            
            $this->showTableHeader($table['row']);
            $this->showTableRows($table['row']);
        }
    }
    
    /**
     * Shows header for complex table
     * @param array $rows
     */
    protected function showTableHeader($rows)
    {
        $this->normalizeData($rows);
        
        if (empty($rows[0]['column'])) return;

        $this->SetFont('Arial', 'B', 9);
        $width = 210 / count($rows[0]['column']);
        $maxY = $y = $this->GetY();
        foreach ($rows[0]['column'] as $index => $column)
        {
            if (!$this->checkIndex($index)) continue;
            
            $this->normalizeData($column);
            $x = $this->GetX();
            $this->MultiCell($width, 6, $column[0]['text'], 0, 'L');
            $maxY = $this->GetY() > $maxY ? $this->GetY() : $maxY;
            $this->SetXY($x + $width, $y);
        }
        
        $this->SetFont('Arial', 'I', 8);
        $this->SetY($maxY); 
    }
    
    /**
     * Shows table rows
     * @param array $rows
     */
    protected function showTableRows($rows)
    {
        $this->normalizeData($rows);
        foreach ($rows as $index => $row)
        {
            if (!$this->checkIndex($index)) continue;
            $this->showTableColumns($row['column']);
        }
    }
    
    /**
     * Shows columns for table row
     * @param array $columns
     */
    protected function showTableColumns($columns)
    {
        $this->normalizeData($columns);
        
        $width = 210 / count($columns);
        $this->correctTableColumns($columns, $width);
        $maxY = $y = $this->GetY();
        
        foreach ($columns as $index => $column)
        {
            if (!$this->checkIndex($index)) continue;
            $x = $this->GetX() + $width;
            $this->MultiCell($width, 6, $this->getAnswer($column), 0, 'L');
            $maxY = $this->GetY() > $maxY ? $this->GetY() : $maxY;
            $this->SetXY($x, $y);
        }
        $this->SetY($maxY);
    }
    
    /**
     * Adds page break if some of columns will exceed page height
     * @param array $columns
     * @param int $width for one column 
     */
    protected function correctTableColumns($columns, $width)
    {
        if ($this->getMaxColumnsLines($columns, $width) * 6 + $this->GetY() > 260)
        {
            $this->addPage();
        }
    }
    
    /**
     * Gets max lines that column wraps
     * @param array $columns
     * @param int $width
     * @return int 
     */
    protected function getMaxColumnsLines($columns, $width)
    {
        $maxHeight = 0;
        foreach ($columns as $column)
        {
            $height = $this->getStringLines($this->getAnswer($column), $width);
            $maxHeight = $height > $maxHeight ? $height : $maxHeight;
        }
        return $maxHeight;
    }
    
    /**
     * Gets lines number that string value will wraps
     * @param string $value
     * @param string $width max width for one line
     * @return int 
     */
    protected function getStringLines($value, $width)
    {
        return ceil($this->GetStringWidth($value) / $width);
    }
    
    /**
     * Shows question-answer block
     * @param array $question
     * @param array $attributes
     */
    protected function showQuestionBlock($question, $attributes)
    {
        //prepare
        $answer   = $this->getAnswer($question);
        $question = cacure_pdf_prepare_string($question['text']);
        
        $this->correctQuestionBlock($question['text'], 92);
        $startX = $this->GetX();
        $startY = $this->GetY();
        $this->MultiCell(92, 6, $question . ' :');
        $endY = $this->GetY();
        $this->SetXY($startX + 94, $startY);
        $this->MultiCell(92, 6, $answer);
        
        if ($endY < $this->GetY())
        {
            $endY = $this->GetY();
        }
        $this->setXY($startX, $endY);
        $this->Ln(4);
    }
    
    /**
     * Adds page break if question exceed page height
     * @param string $question
     * @param int $width max width for one line 
     */
    protected function correctQuestionBlock($question, $width)
    {
        if ($this->getStringLines($question, $width) * 6 + $this->GetY() > 260)
        {
            $this->addPage();
        }
    }
    /**
     * Shows title for questions block
     * @param array $data
     */
    protected function showQuestionsTitle($data)
    { 
        $title = !empty($data['text']) ? $data['text'] : '';
    
        if (!$title) return;
        
        $this->Ln(4);
        $this->SetFont('Arial', 'B', 11);
        $this->MultiCell(0, 6, $title);
        $this->SetFont('Arial', 'I', 8);
        $this->Ln(4);
    }
    
    /**
     * Gets answer from question data
     * @param array $question
     * @return string $answer
     */
    protected function getAnswer($question)
    {
        $answer = $this->getAttributes($question, 'answer');
        
        if (!is_array($question['answer']))
        {
            return !empty($answer['text']) ? $answer['text'] : $question['answer'];
        }
        
        //for multiple answers
        $result = array();
        foreach ($question['answer'] as $index => $answer)
        {
            if (!is_int($index)) continue;
            
            $attributes = $this->getAttributes($question['answer'], $index);
            $result[] = !empty($attributes['text']) ? $attributes['text'] : $answer;
        }
        
        return implode(', ', $result);
    }
    
    /**
     * Gets attributes related with some field
     * @param array $data
     * @param string $name
     * @return array|null 
     */
    protected function getAttributes($data, $name)
    {
        $name .= '_attr';
        return isset($data[$name]) ? $data[$name] : null;
    }
    
    /**
     * Formats data to array(0 => entry)
     * @param array $data
     */
    protected function normalizeData(&$data)
    {
        if (!isset($data[0]))
        {
            $data = array($data);
        }
    }
    
    /**
     * Checks if current index not attribute
     * @param int|string $index
     * @return bool 
     */
    protected function checkIndex($index)
    {
        return is_int($index);
    }
}
?>
