<?php
// il sito da cui è stato preso spunto per il codice: http://forums.devshed.com/php-faqs-and-stickies-167/how-to-program-a-basic-but-secure-login-system-using-891201.html

// Connessione al database.
// Database connection.
require("../config.inc.php");

/* 
 * Se non sono passati parametri significa che sto debuggando, e quindi
 * mostro l'html sottostante.
 * L'unico parametro richiesto è il codice documento.
 * --
 * If there are no params, it means that I'm debugging, and so i show 
 * the html at the end of this source page.
 * The only requested param is the codice documento.
 */
if(!empty($_POST)){
	
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
		$response["message"] = "Errore nella query del codice documento!";
		die(json_encode($response));        
	} 
    
    //qua salvo il CodDocumento corrispondente all'username
	$row=$stmt->fetch();
	//dichiaro una variabile per contenere la dataDelSistema
	$data = date('Y-m-d');
    
    if($row){
    	//controllo se l'utente ha gia un abbonamento valido
		//faccio la query che prende l'ultimo abbonamento acquistato dall'utente
		$query='SELECT DataInizio , Durata, Nome
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
        	//metto in una variabile il nome dell'abbonamento 
            $nomeAbbonamento=$riga['Nome'];      
        	//metto in una variabile la Data di Inizio Abbonamento
			$DataInizio=$riga['DataInizio'];
			//metto in una variabile la durata dell'abbonamento
			$durata=$riga['Durata'];    
            
            
            //mi ricavo la data di fine dell'abbonamento
			list($anno,$mese,$giorno) = explode("-",$DataInizio);
			$DataFine=date("Y-m-d", mktime(0,0,0,$mese,$giorno+$durata,$anno));
            
            if(datediff("G", $data, $DataFine)<=0){
            		//se l'abbonamento non e piu valido
                    
                    //faccio la query per prendere il numero di biglietti dell'utente
                    $query = "SELECT 
                                NumeroBiglietti
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
                        $response["message"] = "Errore nella query del numero biglietti!";
                        die(json_encode($response));        
                    } 
                    
                    //qua salvo il numero di biglietti in una variabile
					$rigabiglietti=$stmt->fetch();
                    
                    $biglietti=$rigabiglietti["NumeroBiglietti"];
                    
                    //adesso controllo se l'ultima corsa è ancora valida
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
                        ':durataconvalida' => 90,
                        ':username' => $_POST['username'],
                        ':durataconvalida2' => 90
                    );
                        
                    // Provo ad eseguire la query, se va in errore invio un json.
                    // Try to execute a query, if it fails I send a json.
                    try {
                        $stmt   = $db->prepare($query);
                        $result = $stmt->execute($query_params);
                    }
                    catch (PDOException $ex) {
                        $response["success"] = 0;
                        $response["message"] = "Errore durante il controllo di validita dell'ultimo biglietto";
                        die(json_encode($response));        
                    }  
                    
                    /*
                     * Ottengo il risultato del db. Se $row è falso non esiste alcun
                     * viaggio.
                     * --
                     * Get the result of the query. If $row is false, there is no ride.
                     */
                    $viaggio = $stmt->fetch();
                    
                    if ($viaggio) {
                        
                        /*
                         * Se il numero ottenuto è positivo significa che l'ultima convalida è
                         * ancora valida, altrimenti il codice va avanti e convalida un biglietto.
                         * --
                         * If the number is greater than 0 it means that the last validation
                         * is still valid. Elsewhere the code goes on and validates another ticket.
                         */
                        if( $viaggio['differenza'] > 0){
                        	//metto in una variabile il tempo rimasto
                            $tempoRimasto=(INT)($viaggio['differenza'] / 60);		
                        }else{
                        	$tempoRimasto=0;
                        }
                    }else{
                    	//se non è ancora stato effettuato alcun viaggio setto le variabili in questo modo
                        $tempoRimasto=0;
                        $durata=90;
                        $ultima=null;
                    }
                    
                    //prendo la data dell'ultima convalida
                    $query = "SELECT 
                                  data, 
                                  durataconvalida
                              FROM viaggio
                              WHERE  coddocumento = :codice
                              ORDER BY data DESC
                              LIMIT 1 ";
                              
                     /*
                     * Aggiunta dei parametri(previene le sql injection, vedi i commenti
                     * in register.php).
                     * --
                     * Add parametra(it prevents sql injections, see comments in
                     * register.php).
                     */
                    $query_params = array(
                        ':codice' => $row['CodDocumento']
                    );
                    
                    // Provo ad eseguire la query, se va in errore invio un json.
                    // Try to execute a query, if it fails I send a json.
                    try {
                            $stmt   = $db->prepare($query);
                            $result = $stmt->execute($query_params);
                    }
                    catch (PDOException $ex) {
                        
                        $response["success"] = 0;
                        $response["message"] = "Errore nella query dell'ultima convalida del biglietto!";
                        die(json_encode($response));        
                    }
                    
                    //qua salvo la durata e l'ultima convalida del biglietto
					$ultimaConvalida=$stmt->fetch();
                    
                    $durata=90;
                    $ultima=$ultimaConvalida["data"];
                    
                    /*
                    //arrivato a questo punto ho tutti i parametri necessari x la mia pagina Home
                    //faccio il json di risposta con tutti i dati
                    
                    $response["success"] = 1;
					$response["message"] = "Ultima corsa ottenuta!";
                    $response["posts"]   = array();
                    
                    $post             = array();
                    $post["biglietti"] = $biglietti;
                    $post["durata"] = 90;
                    $post["data"] = $ultimaConvalida["data"];
                    $post["tempoRimasto"] = $tempoRimasto;
                    
                    //update our repsonse JSON data
					array_push($response["posts"], $post);
                    
                    // echoing JSON response
					echo json_encode($response);
                    */
            
            }else{
            		#####################################################################################################
                    #####################################################################################################
                    #####################################################################################################
            
                    //se entro qua ce un abbonamento valido
                    $biglietti=$nomeAbbonamento;
                    
                    //per quanto riguarda la durata dell'abbonamento, dovrebbe essere salvata in $durata 
                    
                    //qua salvo il tempo rimasto dell'abbonamento
                    $tempoRimasto=datediff("G", $data, $DataFine);
                    
                    $ultima=null;
                    
                    //faccio la query per prendere l'ultima convalida dell'abonamento
                    $query = "SELECT data 
                    			FROM viaggio 
                                WHERE DurataConvalida=:durataConvalida 
                                ORDER BY codViaggio desc LIMIT 1";
                                
                    /*
                    * Aggiunta dei parametri(previene le sql injection, vedi i commenti
                    * in register.php).
                    * --
                    * Add parametra(it prevents sql injections, see comments in
                    * register.php).
                    */
                    $query_params = array(
                        ':durataConvalida' => $durata
                    );
                    
                    // Provo ad eseguire la query, se va in errore invio un json.
                    // Try to execute a query, if it fails I send a json.
                    try {
                        $stmt   = $db->prepare($query);
                        $result = $stmt->execute($query_params);
                    }
                    catch (PDOException $ex) {
                        
                        $response["success"] = 0;
                        $response["message"] = "Errore nella query per prendere l'ultima convalida dell'abbonamento!";
                        die(json_encode($response));        
                    } 
                    
                    //qua salvo il numero di biglietti in una variabile
					$convalidaAbbonamento=$stmt->fetch();
                    
                    if($convalidaAbbonamento){
                    	$ultima=$convalidaAbbonamento["data"];                   	
                    }else{
                    	$ultima=null;
                    }
                    
                    
                    #####################################################################################################
                    #####################################################################################################
                    #####################################################################################################
            }
        }else{
        			//faccio la query per prendere il numero di biglietti dell'utente
                    $query = "SELECT 
                                NumeroBiglietti
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
                        $response["message"] = "Errore nella query del numero biglietti!";
                        die(json_encode($response));        
                    } 
                    
                    //qua salvo il numero di biglietti in una variabile
					$rigabiglietti=$stmt->fetch();
                    
                    $biglietti=$rigabiglietti["NumeroBiglietti"];
                    
                    //adesso controllo se l'ultima corsa è ancora valida
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
                        ':durataconvalida' => 90,
                        ':username' => $_POST['username'],
                        ':durataconvalida2' => 90
                    );
                        
                    // Provo ad eseguire la query, se va in errore invio un json.
                    // Try to execute a query, if it fails I send a json.
                    try {
                        $stmt   = $db->prepare($query);
                        $result = $stmt->execute($query_params);
                    }
                    catch (PDOException $ex) {
                        $response["success"] = 0;
                        $response["message"] = "Errore durante il controllo di validita dell'ultimo biglietto";
                        die(json_encode($response));        
                    }  
                    
                    /*
                     * Ottengo il risultato del db. Se $row è falso non esiste alcun
                     * viaggio.
                     * --
                     * Get the result of the query. If $row is false, there is no ride.
                     */
                    $viaggio = $stmt->fetch();
                    
                    if ($viaggio) {
                        
                        /*
                         * Se il numero ottenuto è positivo significa che l'ultima convalida è
                         * ancora valida, altrimenti il codice va avanti e convalida un biglietto.
                         * --
                         * If the number is greater than 0 it means that the last validation
                         * is still valid. Elsewhere the code goes on and validates another ticket.
                         */
                        if( $viaggio['differenza'] > 0){
                        	//metto in una variabile il tempo rimasto
                            $tempoRimasto=(INT)($viaggio['differenza'] / 60);		
                        }else{
                        	$tempoRimasto=0;
                        }
                    }else{
                    	//se non è ancora stato effettuato alcun viaggio setto le variabili in questo modo
                        $tempoRimasto=0;
                        $durata=90;
                        $ultimaConvalida=null;
                    }
                    
                    //prendo la data dell'ultima convalida
                    $query = "SELECT 
                                  data, 
                                  durataconvalida
                              FROM viaggio
                              WHERE  coddocumento = :codice
                              ORDER BY data DESC
                              LIMIT 1 ";
                              
                     /*
                     * Aggiunta dei parametri(previene le sql injection, vedi i commenti
                     * in register.php).
                     * --
                     * Add parametra(it prevents sql injections, see comments in
                     * register.php).
                     */
                    $query_params = array(
                        ':codice' => $row['CodDocumento']
                    );
                    
                    // Provo ad eseguire la query, se va in errore invio un json.
                    // Try to execute a query, if it fails I send a json.
                    try {
                            $stmt   = $db->prepare($query);
                            $result = $stmt->execute($query_params);
                    }
                    catch (PDOException $ex) {
                        
                        $response["success"] = 0;
                        $response["message"] = "Errore nella query dell'ultima convalida del biglietto!";
                        die(json_encode($response));        
                    }
                    
                    //qua salvo la durata e l'ultima convalida del biglietto
					$ultimaConvalida=$stmt->fetch();
                    
                    $durata=90;
                    $ultima=$ultimaConvalida["data"];
        }
   	}else{
    	$response["success"] = 0;
		$response["message"] = "Errore con il CodiceUtente!";
		die(json_encode($response)); 
    }
    
    //arrivato a questo punto ho tutti i parametri necessari x la mia pagina Home
    //faccio il json di risposta con tutti i dati
                    
    $response["success"] = 1;
	$response["message"] = "Ultima corsa ottenuta!";
    $response["posts"]   = array();
                    
    $post             = array();
    $post["biglietti"] = $biglietti;
    $post["durata"] = $durata;
    $post["data"] = $ultima;
    $post["tempoRimasto"] = $tempoRimasto;
                    
    //update our repsonse JSON data
	array_push($response["posts"], $post);
                    
    // echoing JSON response
	echo json_encode($response);
	
    // Codice html nel caso non vengano passati parametri a questa pagina.
    // html code if there are not params passed to that page.
} else {
?>
	<h1>Download Home Page</h1> 
	<form action="homePage.php" method="post"> 
	    Username:<br /> 
	    <input type="text" name="username" value="" /> 
	    <br /><br /> 
	    <br /><br /> 
	    <input type="submit" value="Download" /> 
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
