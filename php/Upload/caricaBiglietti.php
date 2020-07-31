<?php
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
    if (empty($_POST['username']) || empty($_POST['codiceAbb'])) {
		
        $response["success"] = 0;
        $response["message"] = "Errore nel passaggio dei parametri!";
        
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
	
	//Faccio una if che controlla quale tipo di codiceAbb è stato inserito e quindi quale biglietto aggiungere
	if($_POST['codiceAbb'] == '1'){
		
		//aggiorno il numero dei biglietti dell'utente aggiungendone uno
		$query = "UPDATE utente
					SET numerobiglietti = numerobiglietti + 1
					WHERE username = :username";
    
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
	}else if($_POST['codiceAbb'] == '10'){
		//aggiorno il numero dei biglietti dell'utente aggiungendone dieci
		$query = "UPDATE utente
					SET numerobiglietti = numerobiglietti + 10
					WHERE username = :username";
    
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
	}else if($_POST['codiceAbb'] == '003'){
	
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
                
					//faccio la query per inserire l'abbbonamento per l'utente
						$query = "INSERT INTO possiede (CodDocumento,codAbbonamento,DataInizio)
								VALUES(:codDocumento, :codAbb, :dataInizio)";
					/*
					* Aggiunta dei parametri(previene le sql injection, vedi i commenti
					* in register.php).
					* --
					* Add parametra(it prevents sql injections, see comments in
					* register.php).
					*/
					$query_params = array(
						':codDocumento' => $row['CodDocumento'],
						':codAbb' => $_POST['codiceAbb'],
						':dataInizio' => $data
					);
					// Provo ad eseguire la query, se va in errore invio un json.
					// Try to execute a query, if it fails I send a json.
					try {
						$stmt   = $db->prepare($query);
						$result = $stmt->execute($query_params);
					}
					catch (PDOException $ex) {
						$response["success"] = 0;
						$response["message"] = "Errore nella esecuzione della query per inserire l'abbonamento!";
						die(json_encode($response));        
					} 
				}else{
					$response["success"] = 0;
					$response["message"] = "Hai gia un abbonamento valido!";
					die(json_encode($response));
				}			
			}else{
				//faccio la query per inserire l'abbbonamento per l'utente
						$query = "INSERT INTO possiede (CodDocumento,codAbbonamento,DataInizio)
								VALUES(:codDocumento, :codAbb, :dataInizio)";
					/*
					* Aggiunta dei parametri(previene le sql injection, vedi i commenti
					* in register.php).
					* --
					* Add parametra(it prevents sql injections, see comments in
					* register.php).
					*/
					$query_params = array(
						':codDocumento' => $row['CodDocumento'],
						':codAbb' => $_POST['codiceAbb'],
						':dataInizio' => $data
					);
					// Provo ad eseguire la query, se va in errore invio un json.
					// Try to execute a query, if it fails I send a json.
					try {
						$stmt   = $db->prepare($query);
						$result = $stmt->execute($query_params);
					}
					catch (PDOException $ex) {
						$response["success"] = 0;
						$response["message"] = "Errore nella esecuzione della query per inserire l'abbonamento!";
						die(json_encode($response));        
					} 
			}		
		}else{
			$response["success"] = 0;
			$response["message"] = "Errore con il CodiceUtente!";
			die(json_encode($response));  
		}
	}else if($_POST['codiceAbb'] == '007'){
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
					//faccio la query per inserire l'abbbonamento per l'utente
						$query = "INSERT INTO possiede (CodDocumento,codAbbonamento,DataInizio)
								VALUES(:codDocumento, :codAbb, :dataInizio)";
					/*
					* Aggiunta dei parametri(previene le sql injection, vedi i commenti
					* in register.php).
					* --
					* Add parametra(it prevents sql injections, see comments in
					* register.php).
					*/
					$query_params = array(
						':codDocumento' => $row['CodDocumento'],
						':codAbb' => $_POST['codiceAbb'],
						':dataInizio' => $data
					);
					// Provo ad eseguire la query, se va in errore invio un json.
					// Try to execute a query, if it fails I send a json.
					try {
						$stmt   = $db->prepare($query);
						$result = $stmt->execute($query_params);
					}
					catch (PDOException $ex) {
						$response["success"] = 0;
						$response["message"] = "Errore nella esecuzione della query per inserire l'abbonamento!";
						die(json_encode($response));        
					} 
				}else{
					$response["success"] = 0;
					$response["message"] = "Hai gia un abbonamento valido!";
					die(json_encode($response));
				}			
			}else{
				//faccio la query per inserire l'abbbonamento per l'utente
						$query = "INSERT INTO possiede (CodDocumento,codAbbonamento,DataInizio)
								VALUES(:codDocumento, :codAbb, :dataInizio)";
					/*
					* Aggiunta dei parametri(previene le sql injection, vedi i commenti
					* in register.php).
					* --
					* Add parametra(it prevents sql injections, see comments in
					* register.php).
					*/
					$query_params = array(
						':codDocumento' => $row['CodDocumento'],
						':codAbb' => $_POST['codiceAbb'],
						':dataInizio' => $data
					);
					// Provo ad eseguire la query, se va in errore invio un json.
					// Try to execute a query, if it fails I send a json.
					try {
						$stmt   = $db->prepare($query);
						$result = $stmt->execute($query_params);
					}
					catch (PDOException $ex) {
						$response["success"] = 0;
						$response["message"] = "Errore nella esecuzione della query per inserire l'abbonamento!";
						die(json_encode($response));        
					} 
			}		
		}else{
			$response["success"] = 0;
			$response["message"] = "Errore con il CodiceUtente!";
			die(json_encode($response));  
		}
	}else if($_POST['codiceAbb'] == '030'){
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
					//faccio la query per inserire l'abbbonamento per l'utente
						$query = "INSERT INTO possiede (CodDocumento,codAbbonamento,DataInizio)
								VALUES(:codDocumento, :codAbb, :dataInizio)";
					/*
					* Aggiunta dei parametri(previene le sql injection, vedi i commenti
					* in register.php).
					* --
					* Add parametra(it prevents sql injections, see comments in
					* register.php).
					*/
					$query_params = array(
						':codDocumento' => $row['CodDocumento'],
						':codAbb' => $_POST['codiceAbb'],
						':dataInizio' => $data
					);
					// Provo ad eseguire la query, se va in errore invio un json.
					// Try to execute a query, if it fails I send a json.
					try {
						$stmt   = $db->prepare($query);
						$result = $stmt->execute($query_params);
					}
					catch (PDOException $ex) {
						$response["success"] = 0;
						$response["message"] = "Errore nella esecuzione della query per inserire l'abbonamento!";
						die(json_encode($response));        
					} 
				}else{
					$response["success"] = 0;
					$response["message"] = "Hai gia un abbonamento valido!";
					die(json_encode($response));
				}			
			}else{
				//faccio la query per inserire l'abbbonamento per l'utente
						$query = "INSERT INTO possiede (CodDocumento,codAbbonamento,DataInizio)
								VALUES(:codDocumento, :codAbb, :dataInizio)";
					/*
					* Aggiunta dei parametri(previene le sql injection, vedi i commenti
					* in register.php).
					* --
					* Add parametra(it prevents sql injections, see comments in
					* register.php).
					*/
					$query_params = array(
						':codDocumento' => $row['CodDocumento'],
						':codAbb' => $_POST['codiceAbb'],
						':dataInizio' => $data
					);
					// Provo ad eseguire la query, se va in errore invio un json.
					// Try to execute a query, if it fails I send a json.
					try {
						$stmt   = $db->prepare($query);
						$result = $stmt->execute($query_params);
					}
					catch (PDOException $ex) {
						$response["success"] = 0;
						$response["message"] = "Errore nella esecuzione della query per inserire l'abbonamento!";
						die(json_encode($response));        
					} 
			}		
		}else{
			$response["success"] = 0;
			$response["message"] = "Errore con il CodiceUtente!";
			die(json_encode($response));  
		}
	}else if($_POST['codiceAbb'] == '365'){
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
					//faccio la query per inserire l'abbbonamento per l'utente
						$query = "INSERT INTO possiede (CodDocumento,codAbbonamento,DataInizio)
								VALUES(:codDocumento, :codAbb, :dataInizio)";
					/*
					* Aggiunta dei parametri(previene le sql injection, vedi i commenti
					* in register.php).
					* --
					* Add parametra(it prevents sql injections, see comments in
					* register.php).
					*/
					$query_params = array(
						':codDocumento' => $row['CodDocumento'],
						':codAbb' => $_POST['codiceAbb'],
						':dataInizio' => $data
					);
					// Provo ad eseguire la query, se va in errore invio un json.
					// Try to execute a query, if it fails I send a json.
					try {
						$stmt   = $db->prepare($query);
						$result = $stmt->execute($query_params);
					}
					catch (PDOException $ex) {
						$response["success"] = 0;
						$response["message"] = "Errore nella esecuzione della query per inserire l'abbonamento!";
						die(json_encode($response));        
					} 
				}else{
					$response["success"] = 0;
					$response["message"] = "Hai gia un abbonamento valido!";
					die(json_encode($response));
				}			
			}else{
				//faccio la query per inserire l'abbbonamento per l'utente
						$query = "INSERT INTO possiede (CodDocumento,codAbbonamento,DataInizio)
								VALUES(:codDocumento, :codAbb, :dataInizio)";
					/*
					* Aggiunta dei parametri(previene le sql injection, vedi i commenti
					* in register.php).
					* --
					* Add parametra(it prevents sql injections, see comments in
					* register.php).
					*/
					$query_params = array(
						':codDocumento' => $row['CodDocumento'],
						':codAbb' => $_POST['codiceAbb'],
						':dataInizio' => $data
					);
					// Provo ad eseguire la query, se va in errore invio un json.
					// Try to execute a query, if it fails I send a json.
					try {
						$stmt   = $db->prepare($query);
						$result = $stmt->execute($query_params);
					}
					catch (PDOException $ex) {
						$response["success"] = 0;
						$response["message"] = "Errore nella esecuzione della query per inserire l'abbonamento!";
						die(json_encode($response));        
					} 
			}		
		}else{
			$response["success"] = 0;
			$response["message"] = "Errore con il CodiceUtente!";
			die(json_encode($response));  
		}
	}

	 /*
     * Arrivando a questo punto significa che il viaggio è stato aggiunto
     * correttamente. Lo si segnala.
     * --
     * Coming here means that the ride is correctly added. I message it.
     */
    $response["success"] = 1;
    $response["message"] = "Biglietto aggiunto correttamente!";
    echo json_encode($response);
    
} else {
?>
	<h1>Add Biglietti</h1> 
	<form action="caricaBiglietti.php" method="post">
		Username:<br /> 
	    <input type="text" name="username" value="" /> 
	    <br /><br /> 
	    CodAbbonamento:<br /> 
	    <input type="text" name="codiceAbb" value="" /> 
	    <br /><br /> 
	    <input type="submit" value="Add Biglietti" /> 
	</form>
	<?php
}
?>

<?PHP
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