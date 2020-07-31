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
    if (empty($_POST['username']) ||empty($_POST['oldPassword']) || empty($_POST['newPassword'])) {
		
        $response["success"] = 0;
        $response["message"] = "Inserire tutti i parametri";
        
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
    
    // Prendo le informazioni dell'utente in base all'username.
    // Gets user's info based on the username.
    $query = " 
            SELECT 
                password,
                salt
            FROM utente 
            WHERE 
                username = :username 
        ";
    
    // Impostazione dei parametri ed esecuzione.
    // Set the parametra and execution.
    $query_params = array(
        ':username' => $_POST['username']
    );    
    try {
        $stmt   = $db->prepare($query);
        $result = $stmt->execute($query_params);
    }
    
    catch (PDOException $ex) {
		
		// Messaggio di errore.
		// Error message.
        $response["success"] = 0;
        $response["message"] = "Errore nella query di recupero password!";
        die(json_encode($response));        
    }
    
    /* Questa variabile dice se l'utente ha effettuate con successo il
     * login o meno. Viene inizializzata a false, presupponendo che non
     * sia stato effettuato. Se le credenziali saranno corrette, la
     * variabile verrà cambiata in true.
     * --
     * This variable tells us whether the user has successfully logged
     * in or not. We initialize it to false, assuming they have not.
     * If we determine that they have entered the right details, then 
     * we switch it to true.
     */
	$login_ok = false; 
    
    /* Recupero dei dati dal db. Se &row è false significa che l'username
	 * inserito non è registrato.
	 * --
	 * Retrieve the user data from the database.  If $row is false, then
	 * the username they entered is not registered. 
	 */
    $row = $stmt->fetch();
    if ($row) {
		
        /* Usando la pasword inserita dall'utente, ed il salt salvato sul
		 * database, controllo che essa corrisponda a quella salvata sul
		 * db ripetendo l'operazione fatta al momento della registrazione,
		 * ovvero l'hashing ripetuto.
		 * --
		 * Using the password submitted by the user and the salt stored
		 * in the database, now we check to see whether the password match
		 * by hashing the submitted password and comparing it to the 
		 * hashed version already stored in the database.
		 */
		$check_password = hash('sha256', $_POST['oldPassword'] . $row['salt']);
		for($round = 0; $round < 65536; $round++) {
			$check_password = hash('sha256', $check_password . $row['salt']);
		}
		
		if($check_password === $row['password']) {
			// Se le due password corrispondono, si modifica $login_ok.
			// If they match, swip $login_ok to true.
			$login_ok = true;
		}
    }
    
    //controllo se il login è avvenuto con il successo con la vecchia password
    if ($login_ok) {
    	//se la password attuale e corretta allora inserisco nel db quella nuova
    
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
        $password = hash('sha256', $_POST['newPassword'] . $salt);
        
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
    }else{   	
		// Messaggio di errore.
		// Error message.
        $response["success"] = 0;
        $response["message"] = "Password attuale errata!";
        die(json_encode($response));  
    }
       
    ##########################################################################
    ##########################################################################
    
    /*
     * Arrivando a questo punto significa che il viaggio è stato aggiunto
     * correttamente. Lo si segnala.
     * --
     * Coming here means that the ride is correctly added. I message it.
     */
    $response["success"] = 1;
    $response["message"] = "Password modificata correttamente!";
    echo json_encode($response);
    
    // Codice html nel caso non vengano passati parametri a questa pagina.
    // html code if there are not params passed to that page.
} else {
?>
	<h1>Modifica Password</h1> 
	<form action="cambiaPassword.php" method="post"> 
    	Username:<br /> 
	    <input type="text" name="username" value="" /> 
	    <br /><br /> 
	    Password Attuale:<br /> 
	    <input type="text" name="oldPassword" value="" /> 
	    <br /><br /> 
        Nuova Password:<br /> 
	    <input type="text" name="newPassword" value="" /> 
	    <br /><br />  
	    <input type="submit" value="Modifica" /> 
	</form>
	<?php
}
?>