function getArgMap() {
	var argMap = new Map();
	var args = window.location.search;
	for (var arg of args.split('&')) {
		var item = arg.split('=')[0].replace('?', '');
		var value = arg.split('=')[1];
		
		argMap.set(item, value);
	}
	return argMap;
}

class Login {
	constructor(beforeDiv, afterDiv, pathname, loginListener, logoutListener) {		
		var me = this;
		
		me.pathname = pathname;
		
		var argMap = getArgMap();//new Map();
		
		var resourceRootPath = "/rest/";
		
		$.ajaxSetup({xhrFields:{withCredentials:true}});
		
		function getArgValue(arg) {
			if (argMap.has(arg)) {
				return argMap.get(arg);
			}
			return "";
		}

		var code = getArgValue('code');
		var scope = getArgValue('scope');
		if (code != '' && scope != '') {
			newLogin(code, scope);
		}
		else {
			autoLogin(function(result, value) {
				if (result == 'success') {
			   		afterLogin();
			   		$('#username').text(value);  		
			   		loginListener(value);				
				}
				else if (result == 'failed') {
					beforeLogin();
				}
			});
		}
				
		function afterLogin() {		
	   		$('#' + beforeDiv).hide();
	   		$('#' + afterDiv).show();	
	   		
			$('#' + afterDiv).append('Hello <label id="username">' + name + ' : </label>');
			$('#username').css('font-weight', 'bold');
			$('#' + afterDiv).append('<button id="logout">Logout</button>');
			$('#logout').click(function() {
				logout();
			});
		}
					
		function beforeLogin() {
	   		$('#' + beforeDiv).show();
	   		$('#' + afterDiv).hide();
	   		
			$('#' + beforeDiv).append('<div class="headerBlank"></div>');
			
			$('#' + beforeDiv).append('<div class="title">SilverBullet</div>');
			$('#' + beforeDiv).append('<div class="subtitle">- Source-less software development -</div>');
			
			$('#' + beforeDiv).append('<div class="headerBlank"></div>');
			
			$('#' + beforeDiv).append('<div id="loginDiv"></div>');
			$('#loginDiv').append('<div class="subtitle">Login with:</div>');
			$('#loginDiv').append('<button id="google" class="loginButton">Google</button>');
			$('#loginDiv').append('<button id="facebook" class="loginButton">Facebook</button>');
			$('#loginDiv').append('<button id="twitter" class="loginButton">Twitter</button>');
			$('#loginDiv').append('<button id="yahoo" class="loginButton">Yahoo</button>');
			$('#loginDiv').append('<button id="silverbullet" class="loginButton">SilverBullet</button>');
			
//			$('#facebook').prop('disabled', true);
//			$('#twitter').prop('disabled', true);
//			$('#yahoo').prop('disabled', true);
			
			$('.loginButton').click(function() {
				var redirectUri = window.location.origin + me.pathname;
				var service = $(this).prop('id');
				
				$.ajax({
					type: "GET", 
					url: window.location.origin + resourceRootPath + "getAuthUrl?url=" + redirectUri 
						+ '&service=' + service,
						
					success: function(response) {
						window.location.href = response;
					},
					error: function(response) {
						alert(response);
					}
				});	
					
			});
		}

		function newLogin(code, scope, method) {
			var redirectUri = window.location.origin + me.pathname;
			console.log(code);
			$.ajax({
				type: "GET", 
				url: window.location.origin + resourceRootPath + "newLogin?code=" + code + "&scope=" + scope + "&redirectUri=" + redirectUri,
				success: function(response, status, xhr) {
					//Cookies.set('silverbulletid', response.value2);
					myCookie.set(response.value2);
					
					if (response.key == 'name') {
		 				window.location.href = window.location.origin + me.pathname;
				   	}
				   	
				},
				error: function(response) {
					alert(response);
				}
			});		
		}
		
		function logout() {
			Cookies.remove('SilverBullet');
	   		window.location.href = ".";		   		
	   		
	   		beforeLogin();
			$.ajax({
				type: "GET", 
				url: window.location.origin + resourceRootPath + "logout",
				success: function(response){
					logoutListener();
				},
				error: function(response) {
					alert(response);
				}
			});	
		}
		
		function autoLogin(resultFunc) {
			var me = this;
			$.ajax({
				type: "GET", 
				url: window.location.origin + resourceRootPath + "autoLogin",
				success: function(response) {
					if (response.value != '') {
				   		$('#' + beforeDiv).hide();
				   		$('#' + afterDiv).show();			 
				   		
				   		$('#username').text(response.value);  		

				   		resultFunc('success', response.value);
					}
					else {
						resultFunc('failed', '');
					}
				},
				error: function(response) {
					alert(response);
				}
			});	
		}	
	}
	
}

