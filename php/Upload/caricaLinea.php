<?php
	ini_set('display_startup_errors',1);
    ini_set('display_errors',1);
    error_reporting(-1);
    
// Connessione al database.
// Database connection.
require("../config.inc.php");

/*
1- Controllo che non esista già questa linea tramite il codice
2- Controllo che tutte le fermate passate esistano
3- Inserimento nella tabella linea
4- Inserimento nella tabella tratta
*/

// Se sono passati dei parametri in POST.
// If posted data is not empty.
if (!empty($_POST['NomeLinea']) && !empty($_POST['CodLinea']) && !empty($_POST['Fermate']) && !empty($_POST['orariAndata']) && !empty($_POST['orariRitorno'])) {

    /********************************************************/
    // Controllo nel DB se è già stata inserita questa linea.
    // Check in the DB if there already is that line.
    $query        = " SELECT * 
					FROM linea
					WHERE CodLinea = :CodLinea";

    // Inserisco il valore CodLinea passato in POST.
    // Insert the value of CodLinea passed in POST.
    $query_params = array(
        ':CodLinea' => $_POST['CodLinea']
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
    
    // Se la query trova almeno una riga significa che la linea esiste già.
    // If the query finds at least one line, it means that the line alreay exists.
    $row = $stmt->fetch();
    if ($row) {
        $response["success"] = 0;
        $response["message"] = "La linea con codice " . $row['CodLinea'] . "è già presente nel database" ;
        die(json_encode($response));
    }
    
    /********************************************************/
    // Controllo che tutte le fermate esistano.
    // I check that all the stops exists.
    $fermate = $_POST['Fermate'];
    foreach($fermate as $fermata){
      $query        = " SELECT 1 
                      FROM fermata
                      WHERE CodFermata = :CodFermata";
  
      // Inserisco il valore NomeFermata passato in POST.
      // Insert the value of NomeFermata passed in POST.
      $query_params = array(
          ':CodFermata' => $fermata
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
      
      if ($stmt->rowCount() == 0) {
          $response["success"] = 0;
          $response["message"] = "La fermata con codice " . $fermata . " non è ancora presente nel database";
          die(json_encode($response));
      }
    }  
    
    
    /********************************************************/
    // Se siamo arrivati qui senza incappare in die, si può procedere con l'inserimento.
    // If we arrived here without dying, we can proceed with the insertion.
    $query = "INSERT INTO linea
			(NomeLinea, CodLinea, CodCapolinea, orariAndata, orariRitorno)
			VALUES (:NomeLinea, :CodLinea, :CodCapolinea, :orariAndata, :orariRitorno) ";
	
	// Inserisco il parametro ed avvio la query.
    // I insert the param and I start the query.
    $query_params = array(
        ':NomeLinea' => $_POST['NomeLinea'],
        ':CodLinea' => $_POST['CodLinea'],
        ':CodCapolinea' => $_POST['Fermate'][0],
        ':orariAndata' => $_POST['orariAndata'],
        ':orariRitorno' => $_POST['orariRitorno']
    );

    try {
        $stmt   = $db->prepare($query);
        $result = $stmt->execute($query_params);
    }
    catch (PDOException $ex) {
    
        $response["success"] = 0;
        $response["message"] = "Errore nell'inserimento della linea.";
        die(json_encode($response));
    }
    
    /********************************************************/
    // Inserimento nella tabella tratta.
    // Insertion into tratta.
    $indice = 0;
    while($indice < count($fermate)){
		
		$query = "INSERT INTO tratta
			(CodFermata, CodLinea, Successiva)
			VALUES (:CodFermata, :CodLinea, :Successiva) ";
            
		if($fermate[$indice] == end($fermate)){
			$query_params = array(
				':CodFermata' => $fermate[$indice],
				':CodLinea' => $_POST['CodLinea'],
				':Successiva' => 0
			);
		}else{
			$query_params = array(
				':CodFermata' => $fermate[$indice],
				':CodLinea' => $_POST['CodLinea'],
				':Successiva' => $fermate[$indice+1]
			);
		}
		try {
			$stmt   = $db->prepare($query);
			$result = $stmt->execute($query_params);
		}
		catch (PDOException $ex) {
        
			$response["success"] = 0;
			$response["message"] = "Errore nell'inserimento della tratta.";
			die(json_encode($response));
		}
		$indice++;
	}
	// Se siamo arrivati fin qui invio un messaggio di inserimento avvenuto.
    // If we arrived here, I send a message of insertion completed.
    $response["success"] = 1;
    $response["message"] = "Linea aggiunta corretamente!";  
    die(json_encode($response)); 
    
    
} else {

	// Creazione di un messaggio di errore che sarà il responso JSON.
	// Create error data that will be the JSON response.
  	$response["success"] = 0;
    $response["message"] = "Inserire tutti i parametri.";
    
    
    // Chiusura della pagina e ritorno del respondo.
  	// Kill the page and return the response.
    die(json_encode($response));
}

?>
