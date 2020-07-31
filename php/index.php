<?php
$reg = "wei";
$salt = dechex(mt_rand(0, 2147483647)) . dechex(mt_rand(0, 2147483647));
echo "salt " . $salt . '<br \>';
$password = hash('sha256', $reg . $salt);
for($round = 0; $round < 65536; $round++)
	{
		$password = hash('sha256', $password . $salt);
	} 
echo "update pass " . $password . '<br \>';
$log = "wei";
echo "salt " . $salt . '<br \>';
$check_password = hash('sha256', $log . $salt);
for($round = 0; $round < 65536; $round++)
		{
			$check_password = hash('sha256', $check_password . $salt);
		}
echo "check finale " . $check_password . '<br \>';
if($check_password === $password)
		{
echo "uguale ";
}
?>
