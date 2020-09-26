<?php
session_start();
?>
<!DOCTYPE html>
<html>
<head>
<title>PHP</title>
<meta charset='UTF-8' />
</head>
<body>
<?php
require_once("funkcje.php");
if ($_SESSION['zalogowany'] == 1){
    echo "Zalogowano","  ";
    echo $_SESSION['zalogowanyImie'],"<br>";
}
else
{
    header('Location: index1.php');
}
?>
<br>
<form action="index1.php" method="post">
        <input type="submit" value="wyloguj" name="wyloguj">
    </form>

<form action='user1.php' method='POST' enctype='multipart/form-data'>
    <input type="file" name="myfile">
    <br><input type="submit" , value="przeslij" , name="przeslij">
<?php
if(isset($_POST['przeslij']))
{
    $currentDir = getcwd();
    $uDIR= "/zdjecia/";
    $fileName = $_FILES['myfile']['name'];
    $fileTmpName = $_FILES['myfile']['tmp_name'];
    $fileType = $_FILES['myfile']['type'];
    if($fileName != "" and 
       ($fileType == 'image/png' or $fileType== 'image/jpeg'
       or
       $fileType == 'image/PNG' or $fileType== 'image/JPEG'
    ))
    {
    
        $up = $currentDir. $uDIR . $fileName;
        if(move_uploaded_file($fileTmpName,$up))
            echo "zdjęcie zostało załadowane na serwer FTP";
        else 
            echo "cos poszło nie tak";
    }
}
?>
</body>
</form>
</html>