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
	
	/*
     * Richiedo l'username per controllare se questo codice esiste.
     * --
     * I ask for username for checking that the id cod exists.
     */
    $query = "SELECT 
                username
            FROM utente 
            WHERE 
                coddocumento = :codice ";
	
	/*
     * Aggiunta dei parametri(previene le sql injection, vedi i commenti
     * in register.php).
     * --
     * Add parametra(it prevents sql injections, see comments in
     * register.php).
     */
	$query_params = array(
		':codice' => $_POST['codice']
	);
    
    // Provo ad eseguire la query, se va in errore invio un json.
    // Try to execute a query, if it fails I send a json.
    try {
        $stmt   = $db->prepare($query);
        $result = $stmt->execute($query_params);
    }
    catch (PDOException $ex) {
		
        $response["success"] = 0;
        $response["message"] = "Errore nella connessione con il Database.";
        die(json_encode($response));        
    }
    
    /*
	 * Ottengo il risultato del db. Se $row è falso non esiste il codice
	 * fornito.
	 * --
	 * Get the result of the query. If $row is false, there is no such
	 * id code.
	 */
    $row = $stmt->fetch();
    if (!$row) {		
        $response["success"] = 0;
        $response["message"] = "Il codice documento non è presente nel database.";
        die(json_encode($response));        
    }
	
	/*
	 * Questa query preleva i viaggi dalla tabella viaggio effettuati
	 * dall'utente con codice documento uguale a quello fornito. Ordina
	 * i risultati in ordine decrescente(le ultime corse per prime) e 
	 * prende solo l'ultimo risultato.
	 * --
	 * This query takes the rides from the table viaggio thas has done
	 * the user with the codice documento identical to the given one.
	 * It orders the record in decreasing order(the last ride first)
	 * and takes just the last one.
	 */
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
		':codice' => $_POST['codice']
	);
    
    // Provo ad eseguire la query, se va in errore invio un json.
    // Try to execute a query, if it fails I send a json.
	try {
			$stmt   = $db->prepare($query);
			$result = $stmt->execute($query_params);
	}
	catch (PDOException $ex) {
		
		$response["success"] = 0;
		$response["message"] = "Errore nella richiesta al Database. Per piacere ritenta.";
		die(json_encode($response));        
	}
		

	/*
	 * Ottengo il risultato del db. Se $row è falso significa che l'utente
	 * non ha mai effettuato un viaggio.
	 * --
	 * Get the result of the query. If $row is false, the user has never
	 * done ha ride.
	 */
	$rows = $stmt->fetchAll();
	if ($rows) {
		$response["success"] = 1;
		$response["message"] = "Ultima corsa ottenuta!";
		$response["posts"]   = array();
		
		foreach ($rows as $row) {
			$post             = array();
			$post["data"] = $row["data"];
			$post["durataconvalida"]    = $row["durataconvalida"];
			
			
			//update our repsonse JSON data
			array_push($response["posts"], $post);
		}
		
		// echoing JSON response
		echo json_encode($response);
		
	} else {
		$response["success"] = 0;
		$response["message"] = "L'utente non ha mai effettuato una corsa.";
		die(json_encode($response));
	}
    
    // Codice html nel caso non vengano passati parametri a questa pagina.
    // html code if there are not params passed to that page.
} else {
?>
	<h1>ultimaTratta</h1> 
	<form action="ultimaTratta.php" method="post"> 
	    Codice Documento:<br /> 
	    <input type="text" name="codice" value="" /> 
	    <br /><br /> 
	    <br /><br /> 
	    <input type="submit" value="Register New User" /> 
	</form>
	<?php
}

?>
