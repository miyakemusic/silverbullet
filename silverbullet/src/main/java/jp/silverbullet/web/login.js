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
	constructor(beforeDiv, afterDiv, loginListener, logoutListener) {		
		var me = this;
		
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
					/*
					$.ajax({
						type: "GET", 
						url: window.location.origin + resourceRootPath + "getAuthUrl",
						success: function(response) {
							window.location.href = response;
						},
						error: function(response) {
							alert(response);
						}
					});					
					*/
				}
			});
		}
				
		function afterLogin() {
	//		$('#' + beforeDiv).clear();
	//		$('#' + afterDiv).clear();			
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
	//		$('#' + beforeDiv).clear();
	//		$('#' + afterDiv).clear();
	   		$('#' + beforeDiv).show();
	   		$('#' + afterDiv).hide();
	   		
			$('#' + beforeDiv).append('<div class="headerBlank"></div>');
			
			$('#' + beforeDiv).append('<div class="title">SilverBullet</div>');
			$('#' + beforeDiv).append('<div class="subtitle">- Source-less software development -</div>');
			
			$('#' + beforeDiv).append('<div class="headerBlank"></div>');
			
			$('#' + beforeDiv).append('<div id="loginDiv"></div>');
			$('#loginDiv').append('<div class="subtitle">Login with:</div>');
			$('#loginDiv').append('<button id="google" class="loginButton">Google</button>');
			$('#loginDiv').append('<button id="facebook">Facebook</button>');
			$('#loginDiv').append('<button id="twitter">Twitter</button>');
			$('#loginDiv').append('<button id="yahoo">Yahoo</button>');
			
			$('#facebook').prop('disabled', true);
			$('#twitter').prop('disabled', true);
			$('#yahoo').prop('disabled', true);
			
			$('#google').click(function() {
				var redirectUri = window.location.origin;
				$.ajax({
					type: "GET", 
					url: window.location.origin + resourceRootPath + "getAuthUrl?url=" + redirectUri,
					success: function(response) {
						window.location.href = response;
					},
					error: function(response) {
						alert(response);
					}
				});	
					
			});
		}

		function newLogin(code, scope) {
			var redirectUri = window.location.origin;
			console.log(code);
			$.ajax({
				type: "GET", 
				url: window.location.origin + resourceRootPath + "newLogin?code=" + code + "&scope=" + scope + "&redirectUri=" + redirectUri,
				success: function(response){
					if (response.key == 'name') {
		 				window.location.href = window.location.origin;
		 				/*
				   		afterLogin();			   		
				   		$('#username').text(response.value);  		
				   		loginListener(response.value);
				   		*/
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
				   	//	loginListener(response.value);
				   		
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

