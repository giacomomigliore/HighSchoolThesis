<?php

// Connessione al db.
require("../config.inc.php");

// Seleziono tutte le fermate.
$query = " SELECT *
            FROM fermata 
        ";

// Eseguo la query.
try {
        $stmt   = $db->prepare($query);
        $result = $stmt->execute($query_params);
    }
    catch (PDOException $ex) {
        $response["success"] = 0;
        $response["message"] = "Errore nel database, perfavore riprova.";
        $response["posts"]   = array();
        die(json_encode($response));        
    }
    

// In fine possiamo recuperare tutti i dati.
$rows = $stmt->fetchAll();


if ($rows) {
    $response["success"] = 1;
    $response["message"] = "Post Available!";
    $response["posts"]   = array();
    
    foreach ($rows as $row) {
        $post = array();
        $post["CodFermata"] = $row["CodFermata"];
        $post["NomeFermata"] = $row["NomeFermata"];
        
        
        // Inserisco nell'array del responso.
        array_push($response["posts"], $post);
    }
    
    // Ritorno il responso.
    echo json_encode($response);    
} else {
    $response["success"] = 0;
    $response["message"] = "No Post Available!";
    $response["posts"]   = array();
    die(json_encode($response));
}
?>
