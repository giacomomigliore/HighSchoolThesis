<?php
// il sito da cui è stato preso spunto per il codice: http://forums.devshed.com/php-faqs-and-stickies-167/how-to-program-a-basic-but-secure-login-system-using-891201.html

// Connessione al database.
// Database connection.
require("../config.inc.php");

if (!empty($_POST['CodLinea'])) {

  // Controllo che la linea sia presente nel db. 
  // Check that the path exists.
  $query        = " SELECT 1 
                      FROM linea
                      WHERE CodLinea = :CodLinea";
  
  // Inserisco il valore NomeFermata passato in POST.
  // Insert the value of NomeFermata passed in POST.
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
  
  if ($stmt->rowCount() == 0) {
    $response["success"] = 0;
    $response["message"] = "La linea con codice " . $_POST['CodLinea'] . " non è presente nel database";
    die(json_encode($response));
  }

  // Elimino la linea dalla tabella linea e dalla tabella tratta.
  // I delete the path from the tables linea and tratta.
  $query = "DELETE l, t 
              FROM linea l JOIN tratta t ON l.CodLinea = t.CodLinea
              WHERE l.CodLinea = :CodLinea";
           
  $query_params = array(
    ':CodLinea' => $_POST['CodLinea']
  );
              
  try {
    $stmt   = $db->prepare($query);
    $result = $stmt->execute($query_params);
  }
  catch (PDOException $ex) {
          
    $response["success"] = 0;
    $response["message"] = "Errore nell'eliminazione della linea";
    die(json_encode($response));
  }
  
  // Se siamo arrivati fin qui invio un messaggio di inserimento avvenuto.
  // If we arrived here, I send a message of insertion completed.
  $response["success"] = 1;
  $response["message"] = "Linea eliminata corretamente!";  
  die(json_encode($response));


}else{
	// Creazione di un messaggio di errore che sarà il responso JSON.
	// Create error data that will be the JSON response.
  	$response["success"] = 0;
    $response["message"] = "Inserire il codice della linea da eliminare.";
    
    
    // Chiusura della pagina e ritorno del respondo.
  	// Kill the page and return the response.
    die(json_encode($response));


}
?>
