<?php
	ini_set('display_startup_errors',1);
    ini_set('display_errors',1);
    error_reporting(-1);


// il sito da cui è stato preso spunto per il codice: http://forums.devshed.com/php-faqs-and-stickies-167/how-to-program-a-basic-but-secure-login-system-using-891201.html

// Connessione al database.
// Database connection.
require("../config.inc.php");

/* 
 * Se non sono passati parametri significa che sto debuggando, e quindi
 * mostro l'html sottostante.
 * --
 * If there are no params, it means that I'm debugging, and so i show 
 * the html at the end of this source page.
 */
if (!empty($_POST)) {
    
    /*
     * Se non ci sono tutti i parametri richiesti la pagina finisce
     * e invia un messaggio di errore tramite json.
     * --
     * If there are not every params requested, the page ends(die)
     * and send a error messag through json.
     */ 
    if (empty($_POST['username']) || empty($_POST['cognome']) || empty($_POST['nome']) || empty($_POST['email']) || empty($_POST['indirizzo']) || empty($_POST['nascita']) || empty($_POST['localita']) || empty($_POST['prov']) || empty($_POST['CAP'])) {
		
        $response["success"] = 0;
        $response["message"] = "Inserire tutti i dati!";
        
        /**
         * Die finisce l'esecuzione del codice, e non viene eseguito più
         * il codice sottostante. Invia anche un messaggio json all'app
         * Android, che dovrà interpretare.
         * --
         * Die kills the page and not execute any code below. It also
         * send a json message to the Android app, that has to parse it.
         */
        die(json_encode($response));
    }
    
    ####################################################################
    //faccio la query per aggiornare i dati dell'utente
    $query = "UPDATE
    				utente
               SET Email=:email, Cognome=:cognome, Nome=:nome, Nascita=:nascita, Indirizzo=:indirizzo, Localita=:localita, Prov=:prov, CAP=:CAP             
			   WHERE username = :username";
    
    //aggiungo i parametri
    $query_params = array(
        ':email' => $_POST['email'],
        ':cognome' => $_POST['cognome'],
        ':nome' => $_POST['nome'],
        ':nascita' => $_POST['nascita'],
        ':indirizzo' => $_POST['indirizzo'],
        ':localita' => $_POST['localita'],
        ':prov' => $_POST['prov'],
        ':CAP' => $_POST['CAP'],
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
		$response["message"] = "Email gia in uso!";
		die(json_encode($response));        
	} 
    
    /*
     * Arrivando a questo punto significa che il viaggio è stato aggiunto
     * correttamente. Lo si segnala.
     * --
     * Coming here means that the ride is correctly added. I message it.
     */
    $response["success"] = 1;
    $response["message"] = "Dati aggiornati correttamente!";
    echo json_encode($response);
    
    // Codice html nel caso non vengano passati parametri a questa pagina.
    // html code if there are not params passed to that page.
} else {
?>
	<h1>Update Utente</h1> 
	<form action="aggiornaDatiUtente.php" method="post"> 
	    Username:<br /> 
	    <input type="text" name="username" value="" /> 
	    <br /><br /> 
        Email:<br /> 
	    <input type="text" name="email" value="" /> 
	    <br /><br />
        Cognome:<br /> 
	    <input type="text" name="cognome" value="" /> 
	    <br /><br />
        Nome:<br /> 
	    <input type="text" name="nome" value="" /> 
	    <br /><br />
        Nascita:<br /> 
	    <input type="text" name="nascita" value="" /> 
	    <br /><br />
        Indirizzo:<br /> 
	    <input type="text" name="indirizzo" value="" /> 
	    <br /><br />
        Localita:<br /> 
	    <input type="text" name="localita" value="" /> 
	    <br /><br />
        Provincia:<br /> 
	    <input type="text" name="prov" value="" /> 
	    <br /><br />
        CAP:<br /> 
	    <input type="text" name="CAP" value="" /> 
	    <br /><br />
	    <input type="submit" value="Aggiorna" /> 
	</form>
	<?php
}
?>