<?php
	ini_set('display_startup_errors',1);
    ini_set('display_errors',1);
    error_reporting(-1);
    
// Connessione al database.
// Database connection.
require("../config.inc.php");

// Se sono passati dei parametri in POST.
// If posted data is not empty.
if (!empty($_POST['NomeFermata'])) {

    // Controllo nel DB se è già stata inserita questa fermata.
    // Check in the DB if there already is that stop.
    $query        = " SELECT 1 
					FROM fermata
					WHERE NomeFermata = :NomeFermata";

    // Inserisco il valore NomeFermata passato in POST.
    // Insert the value of NomeFermata passed in POST.
    $query_params = array(
        ':NomeFermata' => $_POST['NomeFermata']
    );
    
    
    try {    
    	// Queste linee eseguono la query.
        // These two statements run the query.
        $stmt   = $db->prepare($query);
        $result = $stmt->execute($query_params);
    }
    catch (PDOException $ex) {        
        // Se queste linee vanno in errore viene visualizzato il messaggio 
        // contenuto nel seguente responso.
        // --
        // If the previous lines get an error we return a response with the 
        // following message.
        $response["success"] = 0;
        $response["message"] = "Errore nella connessione al database, ritenta!";
        die(json_encode($response));
    }
    
    // Se la query trova almeno una riga significa che la fermata esiste già.
    // If the query finds at least one line, it means that the stop alreay exists.
    $row = $stmt->fetch();
    if ($row) {
        $response["success"] = 0;
        $response["message"] = "La fermata è già presente nel database";
        die(json_encode($response));
    }
    
    // Se siamo arrivati qui senza incappare in die, si può procedere con l'inserimento.
    // If we arrived here without dying, we can proceed with the insertion.
    $query = "INSERT INTO fermata
			(NomeFermata)
			VALUES (:NomeFermata) ";
	
	// Inserisco il parametro ed avvio la query.
    // I insert the param and I start the query.
    $query_params = array(
        ':NomeFermata' => $_POST['NomeFermata']
    );

    try {
        $stmt   = $db->prepare($query);
        $result = $stmt->execute($query_params);
    }
    catch (PDOException $ex) {
    
        $response["success"] = 0;
        $response["message"] = "Errore nell'inserimento della fermata.";
        die(json_encode($response));
    }
    
    // Se siamo arrivati fin qui invio un messaggio di inserimento avvenuto.
    // If we arrived here, I send a message of insertion completed.
    $response["success"] = $db->lastInsertId(); ;
    $response["message"] = "Fermata aggiunta corretamente!";  
    die(json_encode($response));  
    
} else {

	// Creazione di un messaggio di errore che sarà il responso JSON.
	// Create error data that will be the JSON response.
  	$response["success"] = 0;
    $response["message"] = "Parametro NomeFermata non ricevuto.";
    
    
    // Chiusura della pagina e ritorno del respondo.
  	// Kill the page and return the response.
    die(json_encode($response));
}

?>
