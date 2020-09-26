<?php session_start(); ?>
<!DOCTYPE html>
<html>
<head>
<title>PHP</title>
<meta charset='UTF-8' />
</head>
<body>

    <?

        echo'<h1>Nasz system</h1>';
        require('funkcje.php');
        //echo "Przesłany login:",$_POST['username'],"<br>";
        //echo "Przesłane hasło:",$_POST['password'],"<br>"; 
        
    if (isset($_POST['wyloguj']))
        $_SESSION['zalogowany'] = 0;

    if(isset($_COOKIE["test"]))
        echo "jest cookie";
    ?>
    
    <form method="post" action="logowanie.php">
        <div><label for="username">Login:</label> <input type="text" name="username" /> </div>
        <div><label for="password">Haslo:</label> <input type="password" name="password" /> </div>
        <input type="submit" value="Zaloguj" name = "check"/>
    </form>
    <form method="get" action="cookie1.php">
        <div><label for="czas">Czas cookie:</label><input type="number" name="czas" /> 
        <input type="submit" value="utworzCookie" name =„utworzCookie” />
    </form>
</body>
</html>