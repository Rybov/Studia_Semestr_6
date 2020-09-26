<?php
session_start();
if (isset($_POST["check"]))
{
    require('funkcje.php');
    foreach (array($osoba1, $osoba2) as $osoba) {
    if($osoba->login== $_POST['username'] && $osoba->haslo == $_POST['password'])
    {
        $_SESSION['zalogowanyImie'] = $osoba->imieNazwisko;
        $_SESSION['zalogowany'] = 1;
        header("Location: user1.php");
    }
    }
    if($_SESSION['zalogowany'] != 1)
        header("Location: index1.php");
}










?>