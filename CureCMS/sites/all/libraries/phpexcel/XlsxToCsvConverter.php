<?php

/**
 * XlsxToCsvConverter
 */
class XlsxToCsvConverter
{
  private $tempDir = '';
  
  private $newcsvfile;
  
  public function setTempDir($tempDir) {
    if (is_string($tempDir)) {
      $this->tempDir = trim($tempDir, '/') . '/';
    }    
  }
  
  public function getTempDir() {
    return $this->tempDir;
  }

  public function getCsvFile($file)
  {
    $this->createCsvFile($file);
    return $this->newcsvfile;
  }
  
	private function createCsvFile($file)
  {
    $str  = str_replace(".xlsx", ".csv", $file);
    $this->newcsvfile = substr($str, strpos($str, "//") + 2, strlen($str));
    $this->newcsvfile = str_replace(" ", "-", $this->newcsvfile);
    $this->newcsvfile = $this->tempDir . 'csv/' .$this->newcsvfile;
    $this->createDir($this->tempDir . 'csv');
    $this->unpackZip($file);
    $this->writeCsvFile();
  }
  
  private function unpackZip($file)
  {
    require_once libraries_get_path('phpexcel', false) . '/PHPExcel/Shared/PCLZip/pclzip.lib.php';
    $archive = new PclZip($file);
    $archive->extract(PCLZIP_OPT_PATH, $this->tempDir . 'bin/');
  }
  
  public function clearTempData()
  {
    $this->removeDir(getcwd() . '/' . $this->tempDir . 'bin');
    $this->removeDir(getcwd() . '/' . $this->tempDir . 'csv');
  }
  
  private function createDir($name)
  {
    if (!is_dir($name)) {
      mkdir($name, 0777);
    }
  }
  
  private function removeDir($path)
  {
    if (file_exists($path) && is_dir($path)) {
      $dirHandle = opendir($path);
      while (false !== ($file = readdir($dirHandle))) {
        if ($file != '.' && $file != '..') {
          $tmpPath = $path . '/' . $file;
          chmod($tmpPath, 0777);
          if (is_dir($tmpPath)) {
            $this->removeDir($tmpPath);
          } 
          else { 
            if(file_exists($tmpPath))	{
              unlink($tmpPath);
            }
          }
        }
      }
      closedir($dirHandle);
      if (file_exists($path)) {
        rmdir($path);
      }
    }
  }
  
  private function writeCsvFile()
  {
    $strings = array();  
    $dir = getcwd();
    $filename = $dir . '/' . $this->tempDir . 'bin/xl/sharedStrings.xml';

    $z = new XMLReader;
    $z->open($filename);

    $csvfile = fopen($this->newcsvfile, "w");

    while ($z->read() && $z->name !== 'si') {
      ob_start();
    }
    while ($z->name === 'si') { 
      $node = new SimpleXMLElement($z->readOuterXML());
      $result = $this->xmlObjToArr($node);   
      if (isset($result['children']['t'][0]['text'])) {
        $val = $result['children']['t'][0]['text'];
        $strings[] = $val;
      }                   
      $z->next('si');
      $result = NULL;      
    }
    ob_end_flush();
    $z->close($filename);

    $filename = $dir . '/' . $this->tempDir . 'bin/xl/worksheets/sheet1.xml';    
    $z = new XMLReader;
    $z->open($filename);
    $rowCount = "0";

    $nums = array("0","1","2","3","4","5","6","7","8","9");
    while ($z->read() && $z->name !== 'row') {
      ob_start();
    }
    while ($z->name === 'row') {  
      $thisrow = array();
      $node = new SimpleXMLElement($z->readOuterXML());
      $result = $this->xmlObjToArr($node); 
      $cells = $result['children']['c'];
      $rowNo = $result['attributes']['r']; 
      $colAlpha = "A";

      foreach ($cells as $cell) {
        if (array_key_exists('v', $cell['children'])) {
          $cellno = str_replace($nums, "", $cell['attributes']['r']);
          for($col = $colAlpha; $col != $cellno; $col++) {
            $thisrow[] = " ";
            $colAlpha++; 
          }
          if(array_key_exists('t', $cell['attributes']) && $cell['attributes']['t'] = 's') {
            $val = $cell['children']['v'][0]['text'];
            $string = $strings[$val] ;
            $thisrow[] = $string;
          } 
          else {
            $thisrow[] = $cell['children']['v'][0]['text'];
          }
        }
        else {
          $thisrow[] = "";
        }
        $colAlpha++;
      }

      $rowLength = count($thisrow);
      $rowCount++;
      $emptyRow = array();

      while ($rowCount < $rowNo) {
        for ($c = 0; $c < $rowLength; $c++) {
          $emptyRow[] = ""; 
        }
        if (!empty($emptyRow)) {
          $this->putToCsv($csvfile, $emptyRow);
        }
        $rowCount++;
      }
      $this->putToCsv($csvfile, $thisrow);      
      $z->next('row');
      $result = NULL;
    }
    $z->close($filename);
    ob_end_flush();
  }
  
  private function putToCsv($handle, $fields, $delimiter = ',', $enclosure = '"', $escape = '\\')
  {
    $first = 1;
    foreach ($fields as $field) {
      if ($first == 0) fwrite($handle, ",");

      $f = str_replace($enclosure, $enclosure.$enclosure, $field);
      if ($enclosure != $escape) {
        $f = str_replace($escape.$enclosure, $escape, $f);
      }
      if (strpbrk($f, " \t\n\r".$delimiter.$enclosure.$escape) || strchr($f, "\000")) {
        fwrite($handle, $enclosure.$f.$enclosure);
      } else {
        fwrite($handle, $f);
      }

      $first = 0;
    }
    fwrite($handle, "\n");
  }
  
  private function xmlObjToArr($obj)
  {
    $namespace = $obj->getDocNamespaces(true);
    $namespace[NULL] = NULL;
    
    $children = array();
    $attributes = array();
       
    $text = trim((string)$obj);
    if (strlen($text) <= 0) {
      $text = NULL;
    }
       
    if (is_object($obj)) {
      foreach ($namespace as $ns => $nsUrl) {
        $objAttributes = $obj->attributes($ns, true);
        foreach ($objAttributes as $attributeName => $attributeValue) {
          $attribName = strtolower(trim((string)$attributeName));
          $attribVal = trim((string)$attributeValue);
          if (!empty($ns)) {
            $attribName = $ns . ':' . $attribName;
          }
          $attributes[$attribName] = $attribVal;
        }
        $objChildren = $obj->children($ns, true);
        foreach ($objChildren as $childName => $child) {
          $childName = strtolower((string)$childName);
          if (!empty($ns)) {
            $childName = $ns.':'.$childName;
          }
          $children[$childName][] = $this->xmlObjToArr($child);
        }
      }
    }
    return array(
      'text' => $text,
      'attributes' => $attributes,
      'children' => $children
    );
  } 
}

