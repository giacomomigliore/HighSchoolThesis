<?php
	ini_set('display_startup_errors',1);
    ini_set('display_errors',1);
    error_reporting(-1);


// il sito da cui è stato preso spunto per il codice: http://forums.devshed.com/php-faqs-and-stickies-167/how-to-program-a-basic-but-secure-login-system-using-891201.html

// Connessione al database.
// Database connection.
require("../config.inc.php");

/* 
 * Se non sono passati parametri significa che sto debuggando, e quindi
 * mostro l'html sottostante.
 * --
 * If there are no params, it means that I'm debugging, and so i show 
 * the html at the end of this source page.
 */
if (!empty($_POST)) {
    
    /*
     * Se non ci sono tutti i parametri richiesti la pagina finisce
     * e invia un messaggio di errore tramite json.
     * --
     * If there are not every params requested, the page ends(die)
     * and send a error messag through json.
     */ 
    if (empty($_POST['username'])) {
		
        $response["success"] = 0;
        $response["message"] = "Inserire l'username";
        
        /**
         * Die finisce l'esecuzione del codice, e non viene eseguito più
         * il codice sottostante. Invia anche un messaggio json all'app
         * Android, che dovrà interpretare.
         * --
         * Die kills the page and not execute any code below. It also
         * send a json message to the Android app, that has to parse it.
         */
        die(json_encode($response));
    }
    
    ####################################################################
    
    //faccio la query per prendere l'email dell'utente
	$query = "SELECT email FROM utente where username=:username";
	/*
	* Aggiunta dei parametri(previene le sql injection, vedi i commenti
	* in register.php).
	* --
	* Add parametra(it prevents sql injections, see comments in
	* register.php).
	*/
	$query_params = array(
		':username' => $_POST['username']
	);
	// Provo ad eseguire la query, se va in errore invio un json.
	// Try to execute a query, if it fails I send a json.
	try {
		$stmt   = $db->prepare($query);
		$result = $stmt->execute($query_params);
	}
	catch (PDOException $ex) {
		$response["success"] = 0;
		$response["message"] = "Errore nel recupero dell'email dell'utente!";
		die(json_encode($response));        
	} 
    
    //recupero l'email
	$riga=$stmt->fetch();
			

	//metto in una variabile l'email dell'utente
	$email=$riga['email'];
    
    //creo una variabile per contenere la nuova password
    $PasswordNuova=generatePassword();
    
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
	$password = hash('sha256', $PasswordNuova . $salt);
	
	// Next we hash the hash value 65536 more times.  The purpose of this is to
	// protect against brute force attacks.  Now an attacker must compute the hash 65537
	// times for each guess they make against a password, whereas if the password
	// were hashed only once the attacker would have been able to make 65537 different 
	// guesses in the same amount of time instead of only one.
	for($round = 0; $round < 65536; $round++)
	{
		$password = hash('sha256', $password . $salt);
	} 
    
    //faccio la query per aggiornare la password dell'utente
	$query='UPDATE utente SET password=:password, salt=:salt WHERE username=:username';
						
	/*
	* Aggiunta dei parametri(previene le sql injection, vedi i commenti
	* in register.php).
	* --
	* Add parametra(it prevents sql injections, see comments in
	* register.php).
	*/
	$query_params = array(
		':password' => $password,
        ':username' => $_POST['username'],
        ':salt' => $salt
	);
			
	// Provo ad eseguire la query, se va in errore invio un json.
	// Try to execute a query, if it fails I send a json.
	try {
		$stmt   = $db->prepare($query);
		$result = $stmt->execute($query_params);
	}
	catch (PDOException $ex) {	
		$response["success"] = 0;
		$response["message"] = "Errore durante l'aggiornamento della password!";
		die(json_encode($response));        
	} 
    
    //mando la mail all'utente con la nuova password
	$oggetto="Nuova Password";
	$messaggio=$PasswordNuova;
	$intestazioni= "From:letsmove.cuneo@gmail.com";
	mail($email, $oggetto, $messaggio, $intestazioni);
  
    
    /*
     * Arrivando a questo punto significa che il viaggio è stato aggiunto
     * correttamente. Lo si segnala.
     * --
     * Coming here means that the ride is correctly added. I message it.
     */
    $response["success"] = 1;
    $response["message"] = "Mail mandata correttamente!";
    echo json_encode($response);
    
    // Codice html nel caso non vengano passati parametri a questa pagina.
    // html code if there are not params passed to that page.
} else {
?>
	<h1>Password Lost</h1> 
	<form action="Password.php" method="post"> 
	    Username:<br /> 
	    <input type="text" name="username" value="" /> 
	    <br /><br /> 
	    <input type="submit" value="Invia" /> 
	</form>
	<?php
}


function generatePassword ( $length = 8 ){

  $password = '';

  $possibleChars = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'; 
    
  $i = 0; 
    
  while ($i < $length) { 

    $char = substr($possibleChars, mt_rand(0, strlen($possibleChars)-1), 1);
        
    if (!strstr($password, $char)) { 
      $password .= $char;
      $i++;
    }

  }

  return $password;

}


?>