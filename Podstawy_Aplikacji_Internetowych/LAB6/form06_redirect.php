<?php session_start(); 

$link = mysqli_connect("localhost", "scott", "tiger", "instytut");
 if (!$link) {
 printf("Connect failed: %s\n", mysqli_connect_error());
 exit();
 }

 if (isset($_POST['id_prac']) &&
 is_numeric($_POST['id_prac']) &&
 is_string($_POST['nazwisko']))
 {
 $sql = "INSERT INTO pracownicy(id_prac,nazwisko) VALUES(?,?)";
 $stmt = $link->prepare($sql);
 $stmt->bind_param("is", $_POST['id_prac'], $_POST['nazwisko']);
 $result = $stmt->execute();
 if (!$result) {
    //printf("Query failed: %s\n", mysqli_error($link));
    $_SESSION["ok"]=2;
    $_SESSION["errorcode"]=mysqli_error($link);
    header("Location: form06_post.php");
 }
else
{
    $_SESSION["ok"]=1;
    header("Location: form06_get.php");
}
 $stmt->close();
 }
 else
 {
     $_SESSION["ok"]=2;
    header("Location: form06_post.php");
}
 $sql = "SELECT * FROM pracownicy";
 $result = $link->query($sql);
 foreach ($result as $v) {
 echo $v["ID_PRAC"]." ".$v["NAZWISKO"]."<br/>";
 }
 $result->free();
 $link->close();














?>