<?php

// Caricamento e connessione al db MySql.
// Load and connessione to the MySql db.
require("config.inc.php");

// Se i dati POST ci sono.
// If there are POST data.
if (!empty($_POST)) {
	
	// Prendo le informazioni dell'utente in base all'username.
    // Gets user's info based on the username.
    $query = " 
            SELECT 
                username, 
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
        $response["message"] = "Errore del database. Prova ancora!";
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
		$check_password = hash('sha256', $_POST['password'] . $row['salt']);
		for($round = 0; $round < 65536; $round++) {
			$check_password = hash('sha256', $check_password . $row['salt']);
		}
		
		if($check_password === $row['password']) {
			// Se le due password corrispondono, si modifica $login_ok.
			// If they match, swip $login_ok to true.
			$login_ok = true;
		}
    }
    
    // Invio di un messaggio relativo al successo del login.
    // Set a message related to the success of the login.
    if ($login_ok) {
		
		// Here I am preparing to store the $row array into the $_SESSION by
		// removing the salt and password values from it.  Although $_SESSION is
		// stored on the server-side, there is no reason to store sensitive values
		// in it unless you have to.  Thus, it is best practice to remove these
		// sensitive values first.
		unset($row['salt']);
		unset($row['password']);
		
		// This stores the user's data into the session at the index 'user'.
		// We will check this index on the private members-only page to determine whether
		// or not the user is logged in.  We can also use it to retrieve
		// the user's details. 
		
        $response["success"] = 1;
        $response["message"] = "Login effettuato con successo!";
        die(json_encode($response));
    } else {
        $response["success"] = 0;
        $response["message"] = "Credenziali non valide!";
        die(json_encode($response));
    }
} else {
?>
		<h1>Login</h1> 
		<form action="login.php" method="post"> 
		    Username:<br /> 
		    <input type="text" name="username" placeholder="username" /> 
		    <br /><br /> 
		    Password:<br /> 
		    <input type="password" name="password" placeholder="password" value="" /> 
		    <br /><br /> 
		    <input type="submit" value="Login" /> 
		</form> 
		<a href="register.php">Register</a>
	<?php
}

?> 

