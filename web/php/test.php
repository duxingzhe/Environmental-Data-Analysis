<form action='' method='post'>
    文本框:<input type='text' name='text'>
    <input type='submit' value='提交',name='sub'>
</form>
<?php
if(!empty($_POST['sub'])){
    echo $_POST['text'];

}

?>