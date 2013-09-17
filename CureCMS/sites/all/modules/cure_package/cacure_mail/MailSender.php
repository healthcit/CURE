<?php
/*
 * Copyright (C) 2013, HealthCare IT, Inc. - All Rights Reserved
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium is strictly prohibited
 * Proprietary and confidential
 */

class MailSender{

  private $options = array(), $empty = 'Empty template';

  public function __construct($type = 'html'){
    if($type == 'html')
      $this->generateHeader();
  }


  private function generateHeader(){
    $this->options['headers']= array(
      'MIME-Version' => '1.0',
      'Content-Type' => 'text/html;charset=utf-8',
    );
  }

  private function send($to = 'alexbuturlakin@gmail.com', $goto = false){
    $this->options['to'] = $to;
    $this->options['headers']['From'] = 'pmills@healthcit.com';
    if($m = drupal_mail_send($this->options))
      drupal_set_message(t('Letter sent to ') . $to, 'message');
    else
      drupal_set_message(t("Can't sent letter to ") . $to, 'error');
    if($goto)
     drupal_goto($goto);
  }

  private function getTemplate($templates = array()){
    $this->options['body'] = isset($templates['body']) ? $templates['body'] : $this->empty;
    $this->options['subject'] = isset($templates['body']) ? $templates['subject'] : $this->empty;
    return $this;
  }


  private function makeSubstitution($vars){
    $this->options['body'] = token_replace($this->options['body'], 'email', $vars,
                $leading = '[', $trailing = ']');
    $this->options['subject'] = token_replace($this->options['subject'], 'email', $vars,
                $leading = '[', $trailing = ']');
    return $this;
  }

  public function sendSubmitModule($vars){
    $vars['goto']  = isset($vars['goto']) ? $vars['goto'] : false;
    $this->getTemplate(array(
      'subject' => variable_get('cacure_mail_template_module_submition_subject', $this->empty),
      'body' => variable_get('cacure_mail_template_module_submition', $this->empty)
    ))->makeSubstitution($vars)->send($vars['to'], $vars['goto']);
  }
  
  public function sendReminderSubmit($vars){
    $vars['goto']  = isset($vars['goto']) ? $vars['goto'] : false;
    $this->getTemplate(array(
      'subject' => variable_get('cacure_mail_template_module_submition_subject', $this->empty),
      'body' => variable_get('cacure_mail_template_submit_reminders', $this->empty)
    ))->makeSubstitution($vars)->send($vars['to'], $vars['goto']);
  }
}
