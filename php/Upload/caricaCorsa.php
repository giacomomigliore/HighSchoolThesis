<?php
	ini_set('display_startup_errors',1);
    ini_set('display_errors',1);
    error_reporting(-1);


// il sito da cui è stato preso spunto per il codice: http://forums.devshed.com/php-faqs-and-stickies-167/how-to-program-a-basic-but-secure-login-system-using-891201.html

// Connessione al database.
// Database connection.
require("../config.inc.php");

/*
 * Riassunto dei controlli:
 * 1- se sono passati parametri;
 * 2- se sono passati tutti i parametri;
 * 3- se le credenziali fornite sono giuste;
 * 4- se l'ultima convalida è ancora valida;
 * 5- se ha ancota biglietti;
 * 6- decremento biglietto.
 * inserimento nella tabella viaggio.
 * --
 * Summarize of checks:
 * 1- if are passed some params;
 * 2- if are passed all params;
 * 3- if the credential are right;
 * 4- if the last validation is still valid;
 * 5- if the user has tickets;
 * insertion in the table viaggio.
 */

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
    if (empty($_POST['username']) || empty($_POST['password']) || empty($_POST['codlinea']) || empty($_POST['durataconvalida'])) {
		
        $response["success"] = 0;
        $response["message"] = "Inserire tutti i campi richiesti, manca: ";
        
        if(empty($_POST['username'])) $response["message"] .= "username ";
        if(empty($_POST['password'])) $response["message"] .= "password ";
        if(empty($_POST['codlinea'])) $response["message"] .= "codlinea ";
        if(empty($_POST['durataconvalida'])) $response["message"] .= "durataconvalida ";
        
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
    
    /*
     * Richiedo l'username, la passowrd e il salt dalla riga il cui
     * username conincide con quello fornito.
     * --
     * I ask for username, password and salt of the line whose
     * username is the same as the given one.
     */
    $query = "SELECT 
                username, 
                password,
                salt
            FROM utente 
            WHERE 
                username = :username";
    
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
        $response["message"] = "Errore nella richiesta al Database. Per piacere ritenta.!";
        die(json_encode($response));        
    }    
    
	/*
	 * Variabile che indica se il login è avvenuto con successo o meno.
	 * Viene inizializzata a false, per poi essere eventualmente cambiata.
	 * --
	 * Variable that says if the user successly logged or not.
	 * It's initialized at false, and if credential are ok will be switched.
	 */
	$login_ok = false; 
    
	/*
	 * Ottengo il risultato del db. Se $row è falso non esiste l'username
	 * fornito.
	 * --
	 * Get the result of the query. If $row is false, there is no such
	 * username.
	 */
    $row = $stmt->fetch();
    if ($row) {
		
        // Esecuzione dell'hashing esattamente come in register.
        // Hashing as in register.
		$check_password = hash('sha256', $_POST['password'] . $row['salt']);
		for($round = 0; $round < 65536; $round++){
			$check_password = hash('sha256', $check_password . $row['salt']);
		}
		
		/*
		 * Se la password scaricata coincide con l'hashing qui calcolato,
		 * allora si cambia $login_ok in true.
		 * --
		 * If the password fetched from the db is the same of the hashing
		 * here computed, flip $login_ok to true.
		 */
		if($check_password === $row['password']){
			$login_ok = true;
		} else {
		
			$response["success"] = 0;
			$response["message"] = "Password errata.";
			die(json_encode($response)); 
		}
    } else {
		
		$response["success"] = 0;
		$response["message"] = "Username non valido.";
		die(json_encode($response));
	}
    
    ####################################################################

    //prendo il codice documento dell'utente
	$query = "SELECT 
				CodDocumento
			FROM utente 
			WHERE 
				username = :username";
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
		$response["message"] = "Errore nella richiesta al Database. Per piacere ritenta.!";
		die(json_encode($response));        
	} 
		
	//qua salvo il CodDocumento corrispondente all'username
	$row=$stmt->fetch();
	//dichiaro una variabile per contenere la dataDelSistema
	$data = date('Y-m-d');
    
    if($row){
    	//controllo se l'utente ha gia un abbonamento valido
		//faccio la query che prende l'ultimo abbonamento acquistato dall'utente
		$query='SELECT DataInizio , Durata
				FROM possiede, abbonamento
				WHERE possiede.codAbbonamento=abbonamento.codAbbonamento and codDocumento=:codDocumento order by codAcquisto desc limit 1';
					
					
					
		/*
		* Aggiunta dei parametri(previene le sql injection, vedi i commenti
		* in register.php).
		* --
		* Add parametra(it prevents sql injections, see comments in
		* register.php).
		*/
		$query_params = array(
			':codDocumento' => $row['CodDocumento']
		);
			
		// Provo ad eseguire la query, se va in errore invio un json.
		// Try to execute a query, if it fails I send a json.
		try {
			$stmt   = $db->prepare($query);
			$result = $stmt->execute($query_params);
		}
		catch (PDOException $ex) {	
			$response["success"] = 0;
			$response["message"] = "Errore durante il controllo dell'abbonamento valido!";
			die(json_encode($response));        
		} 
        
        //recupero la data DataInizio
		$riga=$stmt->fetch();
        
        //controllo se l'utente ha avuto degli abbonamenti
		if($riga){
        	//metto in una variabile la Data di Inizio Abbonamento
			$DataInizio=$riga['DataInizio'];
			//metto in una variabile la durata dell'abbonamento
			$durata=$riga['Durata'];
            
            //mi ricavo la data fi fine dell'abbonamento
			list($anno,$mese,$giorno) = explode("-",$DataInizio);
			$DataFine=date("Y-m-d", mktime(0,0,0,$mese,$giorno+$durata,$anno));
            
            if(datediff("G", $data, $DataFine)<=0){
            		//controllo se l'ultimo biglietto è ancora valido
                    /*
                     * Chiedo la seguente funzione: date_add( data, INTERVAL durataconvalida
                     * MINUTE ) - now() , ovvero sommo alla data in cui è stato convalidato
                     * il ticket la durata della convalida, e sottraggo la data di questo
                     * momento.
                     * --
                     * I ask the following function:date_add( data, INTERVAL durataconvalida
                     * MINUTE ) - now() , i.e. I add the date in which is has been validated
                     * the ticket and the last of the validation, then I subtract the date
                     * of the current moment.
                     */
                     /* only for my_sql 5.5.0 or above
                    $query = "SELECT 
                                TO_SECONDS(DATE_ADD( data, INTERVAL :durataconvalida MINUTE )) - TO_SECONDS(now()) AS differenza
                                FROM viaggio 
                                WHERE coddocumento = (SELECT
                                                    coddocumento
                                                    FROM utente
                                                    WHERE username = :username)
                                ORDER BY TO_SECONDS(DATE_ADD( data, INTERVAL :durataconvalida2 MINUTE )) - TO_SECONDS(now()) DESC
                                LIMIT 1";
                                */
                     // for mysql on alervista that does not supports TO_SECONDS() 
                     // http://stackoverflow.com/questions/4617812/emulating-to-seconds-in-older-versions-of-mysql-5-5-0
                     $query = "SELECT 
                                    CAST(TO_DAYS(DATE_ADD( data, INTERVAL :durataconvalida MINUTE ))*86400+TIME_TO_SEC(DATE_ADD( data, INTERVAL :durataconvalida MINUTE )) AS SIGNED INTEGER)
                                    - CAST(TO_DAYS(NOW())*86400+TIME_TO_SEC(NOW()) AS SIGNED INTEGER) AS differenza
                                    FROM viaggio 
                                    WHERE coddocumento = (SELECT
                                                    coddocumento
                                                    FROM utente
                                                    WHERE username = :username)
                                ORDER BY CAST(TO_DAYS(DATE_ADD( data, INTERVAL :durataconvalida MINUTE ))*86400+TIME_TO_SEC(DATE_ADD( data, INTERVAL :durataconvalida MINUTE )) AS SIGNED INTEGER)
                                    - CAST(TO_DAYS(NOW())*86400+TIME_TO_SEC(NOW()) AS SIGNED INTEGER) DESC
                                LIMIT 1";
                    
                    /*
                     * Aggiunta dei parametri(previene le sql injection, vedi i commenti
                     * in register.php).
                     * --
                     * Add parametra(it prevents sql injections, see comments in
                     * register.php).
                     */
                    $query_params = array(
                        ':durataconvalida' => $_POST['durataconvalida'],
                        ':username' => $_POST['username'],
                        ':durataconvalida2' => $_POST['durataconvalida']
                    );
                        
                    // Provo ad eseguire la query, se va in errore invio un json.
                    // Try to execute a query, if it fails I send a json.
                    try {
                        $stmt   = $db->prepare($query);
                        $result = $stmt->execute($query_params);
                    }
                    catch (PDOException $ex) {
                        $response["success"] = 0;
                        $response["message"] = "Errore nella richiesta al Database. Per piacere ritenta.?";
                        die(json_encode($response));        
                    }    
                    
                    /*
                     * Ottengo il risultato del db. Se $row è falso non esiste alcun
                     * viaggio.
                     * --
                     * Get the result of the query. If $row is false, there is no ride.
                     */
                    $row = $stmt->fetch();
                    if ($row) {
                        
                        /*
                         * Se il numero ottenuto è positivo significa che l'ultima convalida è
                         * ancora valida, altrimenti il codice va avanti e convalida un biglietto.
                         * --
                         * If the number is greater than 0 it means that the last validation
                         * is still valid. Elsewhere the code goes on and validates another ticket.
                         */
                        if( $row['differenza'] > 0){
                            $response["success"] = 0;
                            $response["message"] = "L'ultima convalida dell'utente è ancora valida per " . (INT)($row['differenza'] / 60) . " minuti.";
                            die(json_encode($response));			
                        }
                    }    
                    
          			/*
                    * Chiedo quanti biglietti restano all'utente. Se non ne ha più scateno
                    * un errore.
                    * --
                    * Check how many tickets the user has, if 0 return an error.
                    */
                    $query = "SELECT NumeroBiglietti FROM utente
                                WHERE username = :username";
                
                    // Aggiunta dei parametri.
                    // Adding parametra.
                    $query_params = array(
                        ':username' => $_POST['username']
                    );
            
                    // Esecuzione della query.
                    // Query execution.
                    try {
                        $stmt   = $db->prepare($query);
                        $result = $stmt->execute($query_params);
                    }
                    catch (PDOException $ex) {		
                        $response["success"] = 0;
                        $response["message"] = "Errore nella richiesta di numero dei biglietti.";
                        die(json_encode($response));
                    }
                
                    /*
                    * Ottengo il risultato del db. 
                    * --
                    * Get the result of the query.
                    */
                    $row = $stmt->fetch();
                    if ($row) {
                    
                        /*
                        * Se il risultato è 0, non ha più biglietti.
                        * --
                        * If the number is o, the user has noi more tickets.
                        */
                        if( $row['NumeroBiglietti'] == 0){
                            $response["success"] = 0;
                            $response["message"] = "L'utente non ha più biglietti disponibili.";
                            die(json_encode($response));			
                        }
                    }   
                
                    ####################################################################
                
                    /*
                    * Decremento di uno il numero di biglietti disponibili all'utente.
                    * Siccome il campo numeroBiglietti è UNSIGNED, se l'utente
                    * ha 0 biglietti la query lancia un errore.
                    * --
                    * I decrement the number of ticket of the user by one. Because the
                    * attribute numberoBiglietti is UNSIGNED, ig the user has 0 tickets,
                    * the query raises an error.
                    */
                    $query = "UPDATE utente
                                SET numerobiglietti = numerobiglietti - 1
                                WHERE username = :username";
                
                    // Aggiunta dei parametri.
                    // Adding parametra.
                    $query_params = array(
                        ':username' => $_POST['username']
                    );
            
                    // Esecuzione della query.
                    // Query execution.
                    try {
                        $stmt   = $db->prepare($query);
                        $result = $stmt->execute($query_params);
                    }
                    catch (PDOException $ex) {
                    
                        $response["success"] = 0;
                        $response["message"] = "Errore nel decremento del numero di biglietti.";
                        die(json_encode($response));
                    }
                
                    ####################################################################
                
                    /*
                    * Se si è giunti fino a qui senza incappare in die(), significa
                    * che l'utente è registrato, e che quindi si può prendere una corsa
                    * dal suo db.
                    * La query particolare l'ho vista qui: http://stackoverflow.com/a/9692344/2337094
                    * --
                    * If the program arrived here without dying, it means that the user
                    * exits, an so we can pick a ride from his ones.
                    * I saw that particular query here: http://stackoverflow.com/a/9692344/2337094
                    */
                    $query = "INSERT INTO viaggio( data, durataconvalida, coddocumento, codlinea )
                                SELECT now(), :durataconvalida, coddocumento, :codlinea
                                FROM utente
                                WHERE :username = username";
                
                    // Aggiunta dei parametri.
                    // Adding parametra.
                    $query_params = array(
                        ':durataconvalida' => $_POST['durataconvalida'],
                        ':username' => $_POST['username'],
                        ':codlinea' => $_POST['codlinea']
                    );
                
                    // Esecuzione della query.
                    // Query execution.
                    try {
                        $stmt   = $db->prepare($query);
                        $result = $stmt->execute($query_params);
                    }
                    catch (PDOException $ex) {
                        
                        /*
                        * Se vi è un errore nella insert(per esempio la linea non è presente
                        * tra le chiavi primarie della tabella 'linea'), occorre riaggingere
                        * il biglietto che è stato decrementato con l'istruzione sql precedente.
                        * E' difficile che si presenti questa situazione per i controlli 
                        * implementati nell'app.
                        * --
                        * If there insert raises an error(for instance the linea is not present
                        * among the primary keys of 'linea' table), we need to add the ticket
                        * just decremented with the previous instruction. It's unprobably that 
                        * this situation happens for the controls implemented into the app.
                        */
                        $query = "UPDATE utente
                                    SET numerobiglietti = numerobiglietti + 1
                                    WHERE username = :username";
                    
                        // Aggiunta dei parametri.
                        // Adding parametra.
                        $query_params = array(
                            ':username' => $_POST['username']
                        );
            
                        // Esecuzione della query.
                        // Query execution.
                        try {
                            $stmt   = $db->prepare($query);
                            $result = $stmt->execute($query_params);
                        }
                        catch (PDOException $ex) {
                            /*
                            * L'errore è improbabile perchè è stata fatta una query analoga
                            * poco prima.
                            * --
                            * The error is unlikely becaus is has been executed an analog
                            * query just before.
                            */
                            $response["success"] = 0;
                            $response["message"] = "Errore improbabile, contattare lo sviluppatore!";
                            die(json_encode($response));
                        }
                    
                        $response["success"] = 0;
                        $response["message"] = "Errore nella richiesta di aggiunta dati al Database. Per favore ritenta.";
                        die(json_encode($response));
                    }
			}else{
					//qua inserisco dentro viaggio la corsa con la durata dell'abbonamento
                    //se entro qua ce un abbonamento valido
                                      
                    /*
                     * Se si è giunti fino a qui senza incappare in die(), significa
                     * che l'utente è registrato, e che quindi si può prendere una corsa
                     * dal suo db.
                     * La query particolare l'ho vista qui: http://stackoverflow.com/a/9692344/2337094
                     * --
                     * If the program arrived here without dying, it means that the user
                     * exits, an so we can pick a ride from his ones.
                     * I saw that particular query here: http://stackoverflow.com/a/9692344/2337094
                     */
                    $query = "INSERT INTO viaggio( data, durataconvalida, coddocumento, codlinea )
                                SELECT now(), :durataconvalida, coddocumento, :codlinea
                                FROM utente
                                WHERE :username = username";
                    
                    // Aggiunta dei parametri.
                    // Adding parametra.
                    $query_params = array(
                        ':durataconvalida' => $riga['Durata'],
                        ':username' => $_POST['username'],
                        ':codlinea' => $_POST['codlinea']
                    );
                    
                    // Esecuzione della query.
                    // Query execution.
                    try {
                        $stmt   = $db->prepare($query);
                        $result = $stmt->execute($query_params);                      
                    }
                    catch (PDOException $ex) {
                        
                        $response["success"] = 0;
                        $response["message"] = "Errore durante l'aggiunta del viaggio nel db (parte abbonamento)";
                        die(json_encode($response));
                    } 
			}
        }
        else{
        			//controllo se l'ultimo biglietto è ancora valido
                    /*
                     * Chiedo la seguente funzione: date_add( data, INTERVAL durataconvalida
                     * MINUTE ) - now() , ovvero sommo alla data in cui è stato convalidato
                     * il ticket la durata della convalida, e sottraggo la data di questo
                     * momento.
                     * --
                     * I ask the following function:date_add( data, INTERVAL durataconvalida
                     * MINUTE ) - now() , i.e. I add the date in which is has been validated
                     * the ticket and the last of the validation, then I subtract the date
                     * of the current moment.
                     */
                     $query = "SELECT 
                                    CAST(TO_DAYS(DATE_ADD( data, INTERVAL :durataconvalida MINUTE ))*86400+TIME_TO_SEC(DATE_ADD( data, INTERVAL :durataconvalida MINUTE )) AS SIGNED INTEGER)
                                    - CAST(TO_DAYS(NOW())*86400+TIME_TO_SEC(NOW()) AS SIGNED INTEGER) AS differenza
                                    FROM viaggio 
                                    WHERE coddocumento = (SELECT
                                                    coddocumento
                                                    FROM utente
                                                    WHERE username = :username)
                                ORDER BY CAST(TO_DAYS(DATE_ADD( data, INTERVAL :durataconvalida MINUTE ))*86400+TIME_TO_SEC(DATE_ADD( data, INTERVAL :durataconvalida MINUTE )) AS SIGNED INTEGER)
                                    - CAST(TO_DAYS(NOW())*86400+TIME_TO_SEC(NOW()) AS SIGNED INTEGER) DESC
                                LIMIT 1";
                    
                    /*
                     * Aggiunta dei parametri(previene le sql injection, vedi i commenti
                     * in register.php).
                     * --
                     * Add parametra(it prevents sql injections, see comments in
                     * register.php).
                     */
                    $query_params = array(
                        ':durataconvalida' => $_POST['durataconvalida'],
                        ':username' => $_POST['username'],
                        ':durataconvalida2' => $_POST['durataconvalida']
                    );
                        
                    // Provo ad eseguire la query, se va in errore invio un json.
                    // Try to execute a query, if it fails I send a json.
                    try {
                        $stmt   = $db->prepare($query);
                        $result = $stmt->execute($query_params);
                    }
                    catch (PDOException $ex) {
                        $response["success"] = 0;
                        $response["message"] = "Errore nella richiesta al Database. Per piacere ritenta.?";
                        die(json_encode($response));        
                    }    
                    
                    /*
                     * Ottengo il risultato del db. Se $row è falso non esiste alcun
                     * viaggio.
                     * --
                     * Get the result of the query. If $row is false, there is no ride.
                     */
                    $row = $stmt->fetch();
                    if ($row) {
                        
                        /*
                         * Se il numero ottenuto è positivo significa che l'ultima convalida è
                         * ancora valida, altrimenti il codice va avanti e convalida un biglietto.
                         * --
                         * If the number is greater than 0 it means that the last validation
                         * is still valid. Elsewhere the code goes on and validates another ticket.
                         */
                        if( $row['differenza'] > 0){
                            $response["success"] = 0;
                            $response["message"] = "L'ultima convalida dell'utente è ancora valida per " . (INT)($row['differenza'] / 60) . " minuti.";
                            die(json_encode($response));			
                        }
                    }    
                    
          			/*
                    * Chiedo quanti biglietti restano all'utente. Se non ne ha più scateno
                    * un errore.
                    * --
                    * Check how many tickets the user has, if 0 return an error.
                    */
                    $query = "SELECT NumeroBiglietti FROM utente
                                WHERE username = :username";
                
                    // Aggiunta dei parametri.
                    // Adding parametra.
                    $query_params = array(
                        ':username' => $_POST['username']
                    );
            
                    // Esecuzione della query.
                    // Query execution.
                    try {
                        $stmt   = $db->prepare($query);
                        $result = $stmt->execute($query_params);
                    }
                    catch (PDOException $ex) {		
                        $response["success"] = 0;
                        $response["message"] = "Errore nella richiesta di numero dei biglietti.";
                        die(json_encode($response));
                    }
                
                    /*
                    * Ottengo il risultato del db. 
                    * --
                    * Get the result of the query.
                    */
                    $row = $stmt->fetch();
                    if ($row) {
                    
                        /*
                        * Se il risultato è 0, non ha più biglietti.
                        * --
                        * If the number is o, the user has noi more tickets.
                        */
                        if( $row['NumeroBiglietti'] == 0){
                            $response["success"] = 0;
                            $response["message"] = "L'utente non ha più biglietti disponibili.";
                            die(json_encode($response));			
                        }
                    }   
                
                    ####################################################################
                
                    /*
                    * Decremento di uno il numero di biglietti disponibili all'utente.
                    * Siccome il campo numeroBiglietti è UNSIGNED, se l'utente
                    * ha 0 biglietti la query lancia un errore.
                    * --
                    * I decrement the number of ticket of the user by one. Because the
                    * attribute numberoBiglietti is UNSIGNED, ig the user has 0 tickets,
                    * the query raises an error.
                    */
                    $query = "UPDATE utente
                                SET numerobiglietti = numerobiglietti - 1
                                WHERE username = :username";
                
                    // Aggiunta dei parametri.
                    // Adding parametra.
                    $query_params = array(
                        ':username' => $_POST['username']
                    );
            
                    // Esecuzione della query.
                    // Query execution.
                    try {
                        $stmt   = $db->prepare($query);
                        $result = $stmt->execute($query_params);
                    }
                    catch (PDOException $ex) {
                    
                        $response["success"] = 0;
                        $response["message"] = "Errore nel decremento del numero di biglietti.";
                        die(json_encode($response));
                    }
                
                    ####################################################################
                
                    /*
                    * Se si è giunti fino a qui senza incappare in die(), significa
                    * che l'utente è registrato, e che quindi si può prendere una corsa
                    * dal suo db.
                    * La query particolare l'ho vista qui: http://stackoverflow.com/a/9692344/2337094
                    * --
                    * If the program arrived here without dying, it means that the user
                    * exits, an so we can pick a ride from his ones.
                    * I saw that particular query here: http://stackoverflow.com/a/9692344/2337094
                    */
                    $query = "INSERT INTO viaggio( data, durataconvalida, coddocumento, codlinea )
                                SELECT now(), :durataconvalida, coddocumento, :codlinea
                                FROM utente
                                WHERE :username = username";
                
                    // Aggiunta dei parametri.
                    // Adding parametra.
                    $query_params = array(
                        ':durataconvalida' => $_POST['durataconvalida'],
                        ':username' => $_POST['username'],
                        ':codlinea' => $_POST['codlinea']
                    );
                
                    // Esecuzione della query.
                    // Query execution.
                    try {
                        $stmt   = $db->prepare($query);
                        $result = $stmt->execute($query_params);
                    }
                    catch (PDOException $ex) {
                        
                        /*
                        * Se vi è un errore nella insert(per esempio la linea non è presente
                        * tra le chiavi primarie della tabella 'linea'), occorre riaggingere
                        * il biglietto che è stato decrementato con l'istruzione sql precedente.
                        * E' difficile che si presenti questa situazione per i controlli 
                        * implementati nell'app.
                        * --
                        * If there insert raises an error(for instance the linea is not present
                        * among the primary keys of 'linea' table), we need to add the ticket
                        * just decremented with the previous instruction. It's unprobably that 
                        * this situation happens for the controls implemented into the app.
                        */
                        $query = "UPDATE utente
                                    SET numerobiglietti = numerobiglietti + 1
                                    WHERE username = :username";
                    
                        // Aggiunta dei parametri.
                        // Adding parametra.
                        $query_params = array(
                            ':username' => $_POST['username']
                        );
            
                        // Esecuzione della query.
                        // Query execution.
                        try {
                            $stmt   = $db->prepare($query);
                            $result = $stmt->execute($query_params);
                        }
                        catch (PDOException $ex) {
                            /*
                            * L'errore è improbabile perchè è stata fatta una query analoga
                            * poco prima.
                            * --
                            * The error is unlikely becaus is has been executed an analog
                            * query just before.
                            */
                            $response["success"] = 0;
                            $response["message"] = "Errore improbabile, contattare lo sviluppatore!";
                            die(json_encode($response));
                        }
                    
                        $response["success"] = 0;
                        $response["message"] = "Errore nella richiesta di aggiunta dati al Database. Per favore ritenta.";
                        die(json_encode($response));
                    }
        }
    }else{
    	$response["success"] = 0;
		$response["message"] = "Errore con il CodiceUtente!";
		die(json_encode($response)); 
    }
    ####################################################################
    
    /*
     * Arrivando a questo punto significa che il viaggio è stato aggiunto
     * correttamente. Lo si segnala.
     * --
     * Coming here means that the ride is correctly added. I message it.
     */
    $response["success"] = 1;
    $response["message"] = "Corsa aggiunta correntamente!";
    echo json_encode($response);
    
    // Codice html nel caso non vengano passati parametri a questa pagina.
    // html code if there are not params passed to that page.
} else {
?>
	<h1>aDD VIAGGIO</h1> 
	<form action="caricaCorsa.php" method="post"> 
	    Username:<br /> 
	    <input type="text" name="username" value="" /> 
	    <br /><br /> 
	    Password:<br /> 
	    <input type="password" name="password" value="" /> 
	    <br /><br /> 
	    cod linea:<br /> 
	    <input type="text" name="codlinea" value="" /> 
	    <br /><br /> 
	    durata:<br /> 
	    <input type="text" name="durataconvalida" value="" /> 
	    <br /><br /> 
	    <br /><br /> 
	    <input type="submit" value="add viaggio" /> 
	</form>
	<?php
}

	//funzione differenza delle date, mi serve per verificare se l'abbonamento è ancora valido o meno
    function datediff($tipo, $partenza, $fine){
        switch ($tipo)
        {
            case "A" : $tipo = 365;
            break;
            case "M" : $tipo = (365 / 12);
            break;
            case "S" : $tipo = (365 / 52);
            break;
            case "G" : $tipo = 1;
            break;
        }
        $arr_partenza = explode("-", $partenza);
        $partenza_gg = $arr_partenza[2];
        $partenza_mm = $arr_partenza[1];
        $partenza_aa = $arr_partenza[0];
        $arr_fine = explode("-", $fine);
        $fine_gg = $arr_fine[2];
        $fine_mm = $arr_fine[1];
        $fine_aa = $arr_fine[0];
        $date_diff = mktime(12, 0, 0, $fine_mm, $fine_gg, $fine_aa) - mktime(12, 0, 0, $partenza_mm, $partenza_gg, $partenza_aa);
        $date_diff  = floor(($date_diff / 60 / 60 / 24) / $tipo);
        return $date_diff;
    }

?>
