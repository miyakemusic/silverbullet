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
	constructor(beforeDiv, afterDiv) {		
		var me = this;
	
		$('#' + afterDiv).append('Hello <label id="username">' + name + ' : </label>');
		$('#username').css('font-weight', 'bold');
		$('#' + afterDiv).append('<button id="logout">Logout</button>');
		$('#logout').click(function() {
			logout();
		});
			
		if (localStorage.getItem('oauthCode') != null) {
			login();
			return;
		}
		
		var argMap = getArgMap();//new Map();
/*		var args = window.location.search;
		for (var arg of args.split('&')) {
			var item = arg.split('=')[0].replace('?', '');
			var value = arg.split('=')[1];
			
			argMap.set(item, value);
		}
*/
				
		if (argMap.has("code")) {
			localStorage.setItem('oauthCode', getArgValue('code'));
			localStorage.setItem('oauthScope', getArgValue('scope'));
			login();
			return;
		}
		
		$('#' + beforeDiv).append('<div class="headerBlank"></div>');
		
		$('#' + beforeDiv).append('<div class="title">SilverBullet</div>');
		$('#' + beforeDiv).append('<div class="subtitle">- Source-less software development -</div>');
		
		$('#' + beforeDiv).append('<div class="headerBlank"></div>');
		
		$('#' + beforeDiv).append('<div id="loginDiv"></div>');
		$('#loginDiv').append('<div class="subtitle">Login with:</div>');
		$('#loginDiv').append('<button id="google" class="loginButton">Google</button>');
		$('#loginDiv').append('<button class="loginButton">Facebook</button>');
		$('#loginDiv').append('<button class="loginButton">Twitter</button>');
		$('#loginDiv').append('<button class="loginButton">Yahoo</button>');
		
		$('#google').click(function() {
			login();
		});
		
		function getArgValue(arg) {
			if (argMap.has(arg)) {
				return argMap.get(arg);
			}
			return "";
		}
		
		function login() {
			var code = localStorage.getItem('oauthCode');
			var scope = localStorage.getItem('oauthScope');
			var redirectUri = window.location.origin;
			console.log(code);
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/system/login?code=" + code + "&scope=" + scope + "&redirectUri=" + redirectUri,
			   success: function(response){
			   	if (response.key == 'RedirectAuth') {
			   		localStorage.removeItem('oauthCode');
			   		localStorage.removeItem('oauthScope');
			   		window.location.href = response.value;
			   	}
			   	else if (response.key == 'Complete') {
			   		$('#' + beforeDiv).hide();
			   		$('#' + afterDiv).show();			 
			   		
			   		$('#username').text(response.value);  		
			   		me.loginListener(response.value);
			   	}
			   }
			});		
		}
		
		function logout() {
	   		localStorage.removeItem('oauthCode');
	   		localStorage.removeItem('oauthScope');	
	   		window.location.href = ".";	
	   		
	   		$('#' + beforeDiv).show();
	   		$('#' + afterDiv).hide();
	   		
	   		this.logoutListener();
		}
	}
	

	
	set loginListener(listener) {
		this._loginListener = listener;
	}
	
	get loginListener() {
		return this._loginListener;
	}
	
	set logoutLisnter(listener) {
		this._logoutListener = listener;
	}
	
	get logoutListener() {
		return this._logoutListener;
	}
}

$.ajaxPrefilter( function( options, originalOptions, jqXHR ) {
	var oauthCode = localStorage.getItem('oauthCode');
//	options.path = originalOptions.path + "/" + oauthCode;
	if (originalOptions.url.includes('?')) {
//		options.url = originalOptions.split('?')[0] + '/' + oauthCode + 
//			originalOptions.split('?')[1];
		if (!originalOptions.url.includes('code=')) {
			options.url += '&code=' + oauthCode;
		}
	}
	else {
		//options.url += '/oauthCode';
		options.url += '?code=' + oauthCode;
	}
});

