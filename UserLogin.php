<?php
        $con=mysqli_connect('13.59.96.134','test','1234','mysql','3306');
        $userID=$_POST["userID"];
        $userPassword=$_POST["userPassword"];
        $statement=mysqli_prepare($con,"select userID, userPassword from operator where userID = ? and userPassword = ?");

        mysqli_stmt_bind_param($statement,"ss",$userID,$userPassword);
        mysqli_stmt_execute($statement);
        mysqli_stmt_store_result($statement);
        mysqli_stmt_bind_result($statement,$userID,$userPassword);

        $response=array();
        $response["success"]=false;
        while(mysqli_stmt_fetch($statement)){
                $response["success"]=true;
                $response["userID"]=$userID;
        }
        echo json_encode($response);
?>
