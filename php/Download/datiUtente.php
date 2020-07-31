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
    $query = "SELECT 
				Email, Cognome, Nome, Nascita, Indirizzo, Localita, Prov, CAP
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
		$response["message"] = "Errore nel recupero dei dati dal db!";
		die(json_encode($response));        
	} 
    
    //qua salvo i dati che leggo dal db
	$row=$stmt->fetch();
    
    //controllo se esistono dei dati relativi al username passato in post
    if($row){
    	//arrivato a questo punto ho tutti i parametri necessari x la mia pagina Home
        //faccio il json di risposta con tutti i dati
                        
        $response["success"] = 1;
        $response["message"] = "Dati ottenuti!";
        $response["posts"]   = array();
                        
        $post             = array();
        $post["email"] = $row["Email"];
        $post["cognome"] = $row["Cognome"];
        $post["nome"] = $row["Nome"];
        $post["nascita"] = $row["Nascita"];
        $post["indirizzo"] = $row["Indirizzo"];
        $post["localita"] = $row["Localita"];
        $post["provincia"] = $row["Prov"];
        $post["CAP"] = $row["CAP"];
                        
        //update our repsonse JSON data
        array_push($response["posts"], $post);
                        
        // echoing JSON response
        echo json_encode($response);
    }else{
    	$response["success"] = 0;
		$response["message"] = "Non esiste questo username!";
		die(json_encode($response));
    }	
    // Codice html nel caso non vengano passati parametri a questa pagina.
    // html code if there are not params passed to that page.
} else {
?>
	<h1>Download Dati Utente</h1> 
	<form action="datiUtente.php" method="post"> 
	    Username:<br /> 
	    <input type="text" name="username" value="" /> 
	    <br /><br /> 
	    <br /><br /> 
	    <input type="submit" value="Download" /> 
	</form>
	<?php
}
?>
