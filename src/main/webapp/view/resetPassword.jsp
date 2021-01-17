<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- title -->
    <title>Reset Password</title>
    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.2/css/all.min.css" integrity="sha512-HK5fgLBL+xu6dm/Ii3z4xhlSUyZgTT9tuc/hSrtw6uzJOvgRr2a9jyxxT1ely+B+xFAmJKVSTbpM/CuL7qxO8w==" crossorigin="anonymous" />

    <style>
        .pass_show {
            position: relative
        }

        .pass_show .ptxt {
            position: absolute;
            top: 50%;
            right: 10px;
            z-index: 1;
            color: #f36c01;
            margin-top: -10px;
            cursor: pointer;
            transition: .3s ease all;
        }

        .pass_show .ptxt:hover {
            color: #333333;
        }

        .loader {
            border: 16px solid #f3f3f3; 
            border-top: 16px solid #3498db;
            border-radius: 50%;
            width: 50px;
            height: 50px;
            animation: spin 2s linear infinite;
        }

        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }
        .loader {
            border-top: 16px solid blue;
            border-right: 16px solid green;
            border-bottom: 16px solid red;
            border-left: 16px solid pink;
        }

    </style>


    <body>
        <div class="container mt-5">
            <div class="row">
                <div class="col-sm-4">
                    <h4>Create your new password</h4>
                    <label>New Password</label>
                    <div class="form-group pass_show">
                        <input type="password" onclick="removeResponseMessage()" id="password" value="" class="form-control" placeholder="New Password">
                    </div>
                    <label>Confirm Password</label>
                    <div class="form-group pass_show">
                        <input type="password" onclick="removeResponseMessage()" id="confirm" value="" class="form-control" placeholder="Confirm Password">
                    </div>
                    <p id='response' style="color: red;"></p>
                    <div class="loader" id="ajaxLoader" style="float: right; display: none;"></div>
                    <div class="form-group">
                        <input type="button" id="button" class="btn btn-lg btn-primary btn-block" value="Submit">
                    </div>
                    <input type="hidden" name="username" id="username" value="${username}">
                    <input type="hidden" name="token" id="token" value="${token}">
                </div>
            </div>
        </div>
    </body>
    <script src="https://code.jquery.com/jquery-3.5.1.min.js" integrity="sha256-9/aliU8dGd2tb6OSsuzixeV4y/faTqgFtohetphbbj0=" crossorigin="anonymous"></script>
    <!-- Latest compiled and minified JavaScript -->
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
    <script>
                            function Ajax() {
                                this.processPOST = function (url, data, postFunction) {
                                    $.ajax({
                                        type: 'POST',
                                        url: url,
                                        data: data,
                                        success: function (data, status, xhr) {
                                            //do something with the data via front-end framework
                                            // location.reload();
                                            postFunction(data);
                                        },
                                        error: function (xhr, textStatus, errorMessage) {
                                            // error callback 
                                            switch (xhr.status) {
                                                case 403:
                                                    alert("Error status : " + xhr.status + "   Forbidden....");
                                                    break;
                                                case 404:
                                                    alert("Error status : " + xhr.status + "   Page not found....");
                                                    break;
                                                default:
                                                    alert("Error status : " + xhr.status + "    somthing went wrong...");
                                            }
                                        }
                                    });
                                };
                            }
                            ;
    </script>
    <script>
        // it saves the password to server
        $("#button").on('click', function () {

            $("#response").text("");
            let username = $("#username").val();
            let token = $("#token").val();
            let password = $("#password").val();
            let confirm = $("#confirm").val();
            let passwordVaslidation = passwordCriteria(password, confirm);
            if (passwordVaslidation === "Validated") {
                $("#ajaxLoader").show();
                $("#button").hide();
                var ajax = new Ajax();
                let urlData = "username=" + username + "&token=" + token + "&password=" + password;
                // it sends request to UserAuthController
                ajax.processPOST("/resetPassword", urlData, POSTsaveChangedPassword);
            } else {
                $("#response").text(passwordVaslidation);
            }

        });
        function POSTsaveChangedPassword(res) {
            $("#ajaxLoader").hide();
            $("#button").show();
            if (res.status === 'changed')
                $("#response").text(res.message);
            else if (res.status === 'expired')
                $("#response").text(res.message);
            else
                $("#response").text("Something went wrong");
        }

        // it check user fullfill the password criteria
        function passwordCriteria(password, confirmPassword) {

            if (password.length > 15 || password.length < 8) {
                return "Password must be less than 16 and more than 7 characters in length.";
            }

            var regularExpression = /^(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]/;
            if (!regularExpression.test(password)) {
                return "password should contain atleast one number and one special character";
            }

            // confirm password matching
            if (password !== confirmPassword) {
                return "Confirm password mismatch";
            }

            return "Validated";
        }


        $(document).ready(function () {
            $('.pass_show').append('<span class="ptxt">Show</span>');
        });
        $(document).on('click', '.pass_show .ptxt', function () {
            $(this).text($(this).text() == "Show" ? "Hide" : "Show");
            $(this).prev().attr('type', function (index, attr) {
                return attr == 'password' ? 'text' : 'password';
            });
        });
        function removeResponseMessage() {
            $("#response").text("");
        }
    </script>

</html>