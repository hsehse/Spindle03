<?php
        $con=mysqli_connect('13.59.96.134','test','1234','mysql','3306');
        $result=mysqli_query($con,"select * from temp");

        $response=array();

      /*  while($row=mysqli_fetch_array($result)){
                array_push($response,array("ave_temp"=>$row[0],"max_temp"=>$row[1],"dis_temp"=>$row[2],"jud_temp"=>$row[3]));
        }*/
        while($row=mysqli_fetch_array($result)){
                array_push($response,array("value"=>$row[0]));
        }

        echo json_encode(array("response"=>$response));

        mysqli_close($con);
?>
