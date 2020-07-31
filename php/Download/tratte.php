<?php

// Connessione al database.
require("../config.inc.php");

// Chiedo tutte le tratte presenti nella tabella tratta.
$query = " 
            SELECT *
            FROM tratta 
        ";

// Provo ad eseguire la query, se va in errore invio un json.
try {
        $stmt   = $db->prepare($query);
        $result = $stmt->execute($query_params);
    }
    catch (PDOException $ex) {
        $response["success"] = 0;
        $response["message"] = "Database Error1. Please Try Again!";
    	$response["posts"]   = array();
        die(json_encode($response));        
    }
    

// Ottengo il risultato del db. Se $rows è falso significa che non vi
// sono linee nel db.
$rows = $stmt->fetchAll();


if ($rows) {
    $response["success"] = 1;
    $response["message"] = "Post Available!";
    $response["posts"]   = array();
    
    // Per ogni dato creo un array da inserire in $response["posts"].
    foreach ($rows as $row) {
        $post             = array();
        $post["CodFermata"] = $row["CodFermata"];
        $post["CodLinea"] = $row["CodLinea"];
        $post["Successiva"]    = $row["Successiva"];
        array_push($response["posts"], $post);
    }
    
    // Invio il risultato della query.
    echo json_encode($response);
    
} else {
    $response["success"] = 0;
    $response["message"] = "No Post Available!";
    $response["posts"]   = array();
    die(json_encode($response));
}
?>
