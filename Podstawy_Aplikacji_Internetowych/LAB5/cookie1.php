<?php session_start(); ?>
<!DOCTYPE html>
<html>
<head>
<title>PHP</title>
<meta charset='UTF-8' />
</head>
<body>
<?php
require_once("funkcje.php");
if (isset($_GET["czas"]))
{
    setcookie("test", $_GET["czas"], time() + $_GET["czas"]);
}
?>
<a href="./index1.php"> wstecz</a>
</body>
</html>