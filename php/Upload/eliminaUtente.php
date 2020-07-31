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

	//faccio la query che legge dal db tutti i dati modificabili dall'utente
    $query = "DELETE FROM utente WHERE username=:username";
                
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
		$response["message"] = "Errore durante l'eliminazione dell'utente!";
		die(json_encode($response));        
	} 
    
    /*
     * Arrivando a questo punto significa che il viaggio è stato aggiunto
     * correttamente. Lo si segnala.
     * --
     * Coming here means that the ride is correctly added. I message it.
     */
    $response["success"] = 1;
    $response["message"] = "Utente eliminato correttamente!";
    echo json_encode($response);
    
    
    
    // Codice html nel caso non vengano passati parametri a questa pagina.
    // html code if there are not params passed to that page.
} else {
?>
	<h1>Elimina Utente</h1> 
	<form action="eliminaUtente.php" method="post"> 
	    Username:<br /> 
	    <input type="text" name="username" value="" /> 
	    <br /><br /> 
	    <br /><br /> 
	    <input type="submit" value="Elimina" /> 
	</form>
	<?php
}
?>
