<?php
# il sito da cui è stato preso spunto per il codice: http://forums.devshed.com/php-faqs-and-stickies-167/how-to-program-a-basic-but-secure-login-system-using-891201.html

/*
Our "config.inc.php" file connects to database every time we include or require
it within a php script.  Since we want this script to add a new user to our db,
we will be talking with our database, and therefore,
let's require the connection to happen:
*/
require("config.inc.php");

//if posted data is not empty
if (!empty($_POST)) {
    //If the username or password is empty when the user submits
    //the form, the page will die.
    //Using die isn't a very good practice, you may want to look into
    //displaying an error message within the form instead.  
    //We could also do front-end form validation from within our Android App,
    //but it is good to have a have the back-end code do a double check.
    if (empty($_POST['username']) || empty($_POST['password']) || empty($_POST['codDoc']) || empty($_POST['email']) || empty($_POST['cognome']) || empty($_POST['nome']) || empty($_POST['nascita']) || empty($_POST['indirizzo']) || empty($_POST['localita']) || empty($_POST['prov']) || empty($_POST['CAP'])) {
		        
        // Create some data that will be the JSON response 
        $response["success"] = 0;
        $response["message"] = "Please Enter All the Credential";
        
        //die will kill the page and not execute any code below, it will also
        //display the parameter... in this case the JSON data our Android
        //app will parse
        die(json_encode($response));
    }
    
    //if the page hasn't died, we will check with our database to see if there is
    //already a user with the username specificed in the form.  ":user" is just
    //a blank variable that we will change before we execute the query.  We
    //do it this way to increase security, and defend against sql injections
    $query        = " SELECT 1 
					FROM utente 
					WHERE username = :user";

    // This contains the definitions for any special tokens that we place in
    // our SQL query.  In this case, we are defining a value for the token
    // :username.  It is possible to insert $_POST['username'] directly into
    // your $query string; however doing so is very insecure and opens your
    // code up to SQL injection exploits.  Using tokens prevents this.
    // For more information on SQL injections, see Wikipedia:
    // http://en.wikipedia.org/wiki/SQL_Injection 
    $query_params = array(
        ':user' => $_POST['username']
    );
    
    //Now let's make run the query:
    try {
        // These two statements run the query against your database table. 
        $stmt   = $db->prepare($query);
        $result = $stmt->execute($query_params);
    }
    catch (PDOException $ex) {
        // For testing, you could use a die and message. 
        //die("Failed to run query: " . $ex->getMessage());
        
        //or just use this use this one to product JSON data:
        $response["success"] = 0;
        $response["message"] = "Database Error1. Please Try Again!";
        die(json_encode($response));
    }
    
    // The fetch() method returns an array representing the "next" row from
    // the selected results, or false if there are no more rows to fetch.
    $row = $stmt->fetch();
    
    
    // If a row was returned, then we know a matching username was found in
    // the database already and we should not allow the user to continue. 
    if ($row) {
        // For testing, you could use a die and message. 
        //die("This username is already in use");
        
        //You could comment out the above die and use this one:
        $response["success"] = 0;
        $response["message"] = "I'm sorry, this username is already in use";
        die(json_encode($response));
    }
    
    // Now we perform the same type of check for the email address, in order
    // to ensure that it is unique.
	$query = "
		SELECT
			1
		FROM utente
		WHERE
			email = :email
	";
	
	$query_params = array(
		':email' => $_POST['email']
	);
	
	try
	{
		$stmt = $db->prepare($query);
		$result = $stmt->execute($query_params);
	}
	catch(PDOException $ex)
	{
		$response["success"] = 0;
        $response["message"] = "Database Error1. Please Try Again!";
        die(json_encode($response));
	}
	
	$row = $stmt->fetch();
	
	if($row)
	{
		$response["success"] = 0;
        $response["message"] = "I'm sorry, this email is already in use";
        die(json_encode($response));
	} 
	
	
    
    
    // Now we perform the same type of check for the codice documento,
    // in order to ensure that it is unique.
	$query = "
		SELECT
			1
		FROM utente
		WHERE
			codDocumento = :codDoc
	";
	
	$query_params = array(
		':codDoc' => $_POST['codDoc']
	);
	
	try
	{
		$stmt = $db->prepare($query);
		$result = $stmt->execute($query_params);
	}
	catch(PDOException $ex)
	{
		$response["success"] = 0;
        $response["message"] = "Database Error1. Please Try Again!";
        die(json_encode($response));
	}
	
	$row = $stmt->fetch();
	
	if($row)
	{
		$response["success"] = 0;
        $response["message"] = "I'm sorry, this codice documento is already in use";
        die(json_encode($response));
	} 
    
    //If we have made it here without dying, then we are in the clear to 
    //create a new user.  Let's setup our new query to create a user.  
    //Again, to protect against sql injects, user tokens such as :user and :pass
    $query = "INSERT INTO utente
			(CodDocumento, username, password, salt, cognome, nome, email, numerobiglietti, indirizzo, nascita, localita, prov, CAP)
			VALUES (:codD, :user, :pass, :salt, :cognome, :nome,  :email, 0, :indirizzo, :nascita, :localita, :prov, :CAP) ";
        
    // A salt is randomly generated here to protect again brute force attacks
	// and rainbow table attacks.  The following statement generates a hex
	// representation of an 8 byte salt.  Representing this in hex provides
	// no additional security, but makes it easier for humans to read.
	// For more information:
	// http://en.wikipedia.org/wiki/Salt_%28cryptography%29
	// http://en.wikipedia.org/wiki/Brute-force_attack
	// http://en.wikipedia.org/wiki/Rainbow_table
	$salt = dechex(mt_rand(0, 2147483647)) . dechex(mt_rand(0, 2147483647));
	
	// This hashes the password with the salt so that it can be stored securely
	// in your database.  The output of this next statement is a 64 byte hex
	// string representing the 32 byte sha256 hash of the password.  The original
	// password cannot be recovered from the hash.  For more information:
	// http://en.wikipedia.org/wiki/Cryptographic_hash_function
	$password = hash('sha256', $_POST['password'] . $salt);
	
	// Next we hash the hash value 65536 more times.  The purpose of this is to
	// protect against brute force attacks.  Now an attacker must compute the hash 65537
	// times for each guess they make against a password, whereas if the password
	// were hashed only once the attacker would have been able to make 65537 different 
	// guesses in the same amount of time instead of only one.
	for($round = 0; $round < 65536; $round++)
	{
		$password = hash('sha256', $password . $salt);
	} 
	
	//Again, we need to update our tokens with the actual data:
    $query_params = array(
        ':user' => $_POST['username'],
        ':pass' => $password, 
        ':salt' => $salt, 
        ':codD' => $_POST['codDoc'],
        ':cognome' => $_POST['cognome'],
        ':nome' => $_POST['nome'],
        ':email' => $_POST['email'],
        ':indirizzo' => $_POST['indirizzo'],
        ':nascita' => $_POST['nascita'],
        ':localita' => $_POST['localita'],
        ':prov' => $_POST['prov'],
        ':CAP' => $_POST['CAP']
    );

    //time to run our query, and create the user
    try {
        $stmt   = $db->prepare($query);
        $result = $stmt->execute($query_params);
    }
    catch (PDOException $ex) {
        // For testing, you could use a die and message. 
        //die("Failed to run query: " . $ex->getMessage());
        
        //or just use this use this one:
        $response["success"] = 0;
        $response["message"] = "Database Error2. Please Try Again!";
        die(json_encode($response));
    }
    
    //If we have made it this far without dying, we have successfully added
    //a new user to our database.  We could do a few things here, such as 
    //redirect to the login page.  Instead we are going to echo out some
    //json data that will be read by the Android application, which will login
    //the user (or redirect to a different activity, I'm not sure yet..)
    $response["success"] = 1;
    $response["message"] = "Username Successfully Added!";
    echo json_encode($response);
    
    //for a php webservice you could do a simple redirect and die.
    //header("Location: login.php"); 
    //die("Redirecting to login.php");
    
    
} else {
?>
	<h1>Register</h1> 
	<form action="register.php" method="post"> 
	    Codice Documento:<br /> 
	    <input type="text" name="codDoc" value="" /> 
	    <br /><br /> 
	    Username:<br /> 
	    <input type="text" name="username" value="" /> 
	    <br /><br /> 
	    Password:<br /> 
	    <input type="password" name="password" value="" /> 
	    <br /><br /> 
	    Email:<br /> 
	    <input type="text" name="email" value="" /> 
	    <br /><br /> 
	    Nome:<br /> 
	    <input type="text" name="nome" value="" /> 
	    <br /><br /> 
	    Cognome:<br /> 
	    <input type="text" name="cognome" value="" /> 
	    <br /><br /> 
	    indirizzo:<br /> 
	    <input type="text" name="indirizzo" value="" /> 
	    <br /><br /> 
	    nascita:<br /> 
	    <input type="text" name="nascita" value="" /> 
	    <br /><br /> 
	    localita:<br /> 
	    <input type="text" name="localita" value="" /> 
	    <br /><br /> 
	    provincia:<br /> 
	    <input type="text" name="prov" value="" /> 
        <br /><br /> 
	    CAP:<br /> 
	    <input type="text" name="CAP" value="" /> 
	    <br /><br /> 
	    <input type="submit" value="Register New User" /> 
	</form>
	<?php
}

?>
