<?php
$conn=mysqli_connect("localhost","root","","spindle");
$value=$_GET['value'];

$sql="INSERT INTO temp VALUES('$value')";
$result = mysqli_query($conn,$sql);
if($result===false){
  echo mysqli_error($conn);
}

mysqli_close($conn);
?>
