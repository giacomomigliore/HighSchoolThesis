<?php

// Connessione al database.
require("../config.inc.php");

// Chiedo tutte le linee presenti nella tabella linea.
$query = "SELECT *
            FROM linea ";

// Provo ad eseguire la query, se va in errore invio un json.
try {
        $stmt   = $db->prepare($query);
        $result = $stmt->execute($query_params);
    }
    catch (PDOException $ex) {		
        $response["success"] = 0;
        $response["message"] = "Errore nella richiesta al Database. Per piacere ritenta.";
    	$response["posts"]   = array();
        die(json_encode($response));        
    }
    

/*
 * Ottengo il risultato del db. Se $rows è falso significa che non vi
 * sono linee nel db.
 */
$rows = $stmt->fetchAll();
if ($rows) {
    $response["success"] = 1;
    $response["message"] = "Post Available!";
    // Creo un array contenente tutte le linee.
    $response["posts"]   = array();
    
    // Per ogni dato creo un array da inserire in $response["posts"].
    foreach ($rows as $row) {
        $post = array();
        $post["CodLinea"] = $row["CodLinea"];
        $post["NomeLinea"] = $row["NomeLinea"];
        $post["CodCapolinea"] = $row["CodCapolinea"];
        $post["orariAndata"] = $row["orariAndata"];
        $post["orariRitorno"] = $row["orariRitorno"];
        array_push($response["posts"], $post);
    }
    
    // Invio il risultato della query.
    echo json_encode($response);
    
} else {
    $response["success"] = 0;
    $response["message"] = "Nessuna linea presente nel db!";
    $response["posts"]   = array();
    die(json_encode($response));
}
?>
