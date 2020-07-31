 <?php
#http://forums.devshed.com/php-faqs-and-stickies-167/how-to-program-a-basic-but-secure-login-system-using-891201.html
    // First we execute our common code to connection to the database and start the session
    require("common.php");
    
    // We remove the user's data from the session
    unset($_SESSION['user']);
    
    // We redirect them to the login page
    header("Location: login.php");
    die("Redirecting to: login.php"); 
?>
