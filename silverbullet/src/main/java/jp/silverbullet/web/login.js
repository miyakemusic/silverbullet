class Login {
	constructor(div, loginListener) {
		var me = this;
		
		if (localStorage.getItem('oauthCode') != null) {
			login();
			return;
		}
		
		var argMap = new Map();
		
		var args = window.location.search;
		for (var arg of args.split('&')) {
			var item = arg.split('=')[0].replace('?', '');
			var value = arg.split('=')[1];
			
			argMap.set(item, value);
		}
				
		if (argMap.has("code")) {
			localStorage.setItem('oauthCode', getArgValue('code'));
			localStorage.setItem('oauthScope', getArgValue('scope'));
//			alert('Key');
			login();
			return;
		}
		
		$('#' + div).append('<div class="headerBlank"></div>');
		
		$('#' + div).append('<div class="title">SilverBullet</div>');
		$('#' + div).append('<div class="subtitle">- Source-less software development -</div>');
		
		$('#' + div).append('<div class="headerBlank"></div>');
		
		$('#' + div).append('<div id="loginDiv"></div>');
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
			console.log(code);
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/system/login?code=" + code + "&scope=" + scope,
			   success: function(response){
			   	if (response.key == 'RedirectAuth') {
			   		localStorage.removeItem('oauthCode');
			   		localStorage.removeItem('oauthScope');
//			   		alert('removeKey');
			   		window.location.href = response.value;
			   	}
			   	else if (response.key == 'Complete') {
			   		
			   		loginListener(response.value);
			   	}
			   }
			});		
		}
	}
	
	logout() {
   		localStorage.removeItem('oauthCode');
   		localStorage.removeItem('oauthScope');	
//   		alert('removeKey');
   		window.location.href = ".";	
	}
}

$.ajaxPrefilter( function( options, originalOptions, jqXHR ) {
	var oauthCode = localStorage.getItem('oauthCode');
//	options.path = originalOptions.path + "/" + oauthCode;
	if (originalOptions.url.includes('?')) {
		if (!originalOptions.url.includes('code=')) {
			options.url += '&code=' + oauthCode;
		}
	}
	else {
		options.url += '?code=' + oauthCode;
	}
});

