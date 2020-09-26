<?php session_start(); 

if(isset($_SESSION["ok"]))
{
if($_SESSION["ok"]==2)
{
    printf("Error<br>");
}
if (isset($_SESSION['errorcode']))
{
    printf($_SESSION["errorcode"]);
}
}
$_SESSION["ok"]=0;
$_SESSION["errorcode"]="";

print<<<KONIEC
 <html>
 <head>
 <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
 </head>
 <body>
 <form action="form06_redirect.php" method="POST">
 id_prac <input type="text" name="id_prac">
 nazwisko <input type="text" name="nazwisko">
 <input type="submit" value="Wstaw">
 <input type="reset" value="Wyczysc">
 </form>
KONIEC;







?>