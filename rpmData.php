<?php
$con=mysqli_connect('13.59.96.134','test','1234','mysql','3306');
$result=mysqli_query($con,"select * from rpm");

$response=array();

while($row=mysqli_fetch_array($result)){
        array_push($response,array("value"=>$row[0]));
}

echo json_encode(array("response"=>$response));

mysqli_close($con);
?>
