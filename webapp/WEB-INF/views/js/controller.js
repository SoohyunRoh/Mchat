/* APP */
var app = angular.module('myChat',['ngFileUpload']);

/* CONTROLLER */4
app.controller('MyChatCtrl', function($scope, $http, $timeout, $window, socket, storage, Upload){

	$scope.fileUpload = function() {
		$scope.uploadPercent = "";
		Upload.upload({
            url: 'upload',
            data: {
            	file: $scope.file, 
            	pid: $scope.chatStorage.myStatus.pid
            }
		}).progress(function(e) {    
			var progressPercentage = parseInt(100.0 * e.loaded / e.total);
	        $scope.uploadPercent = progressPercentage + '%';
	        
		}).success(function (data, status, headers, config) {
			$scope.uploadPercent = "DONE";
			$scope.chatStorage.myStatus.portraitFile = data.result;

			var data = {
				CODE:"A06",
				PID:$scope.chatStorage.myStatus.pid,
				FILE:data.result
			}
			socket.send(data);			//A04: Change user status
	    });
	}

	$scope.forms = {};
	$scope.chatStorage = storage.get();
	$scope.changeUserStatus = function(){
		var data = {
			CODE:"A04",
			PID:$scope.chatStorage.myStatus.pid,
			STATUS:$scope.chatStorage.myStatus.status
		}
		socket.send(data);			//A04: Change user status
	}
	
	$scope.changeUserStatusMessage = function(){
		var data = {
			CODE:"A05",
			PID:$scope.chatStorage.myStatus.pid,
			MESSAGE:$scope.chatStorage.myStatus.statusMessage
		}
		socket.send(data);			//A05: Change user status message
	}
	
	//Filter function
	$scope.messageNotEmpty = function(item){
		if(item.message != null && item.message.length !== 0) return true;
		else return false;
	}
	
	$scope.loginEnterKey = function(event) {
		if (event.which === 13){
			$scope.login();
		}
	}
	
	$scope.hasNewMessage = function(pid){
		var ret = false;
		var idx = $scope.chatStorage.friendList.findIndex(function(item){
			return item.pid == pid;
		});

		if(idx > -1){
			var tempMessage = $scope.chatStorage.friendList[idx].message;
			if(tempMessage[tempMessage.length - 1].read == 'N')ret = true;
		}
		return ret;
	}
	
	$scope.showPidOnly = function(item){
		if($scope.chatStorage.showPID != '00000000' && item.pid == $scope.chatStorage.showPID)
			return true;
		else false;
	}
	
	$scope.logout = function(){
		var data = {
			CODE:"A02",
			PID:$scope.chatStorage.myStatus.pid
		}
		socket.send(data);
		
		$http({
			method: 'POST',
			url: 'logoutRequest'
		}).then(
			function(response){
				$scope.chatStorage = {};
				$window.location.reload();
			},
			function(response){
				console.log("Fail");
			}
		);
	}
	
	$scope.login = function(){
		var message = "";
		var tmpMessage = "";

		if(typeof $scope.email == "undefined"){
			message = "Please input correct email address."
		}
		if(typeof $scope.password == "undefined"){
			tmpMessage = "Password length must be more then 3 characters."
			if(message != ""){
				message += "\n" + tmpMessage;
			}else{
				message = tmpMessage;
			}
		}
		if(message != ""){
			alert(message);
		} else {
			$http({
				method: 'POST',
				url: 'loginRequest',
				data: {
					email: $scope.email,
					password: $scope.password
				}
			}).then(
				function(response){
					if(response.data.result != "-1"){
						$scope.loadingData(response.data.result);
						$('#loginModal').modal('close');
					}else{
						Materialize.toast('Invalid user information.Try again.', 4000, 'red');
					}
				},
				function(response){
					alert("fail:"+response);
				}
			);
		}
	}
	
	$scope.signUp = function(){
		var message = "";
		var tmpMessage = "";

		if(typeof $scope.email == "undefined"){
			message = "* Please input correct email address."
		}
		if(typeof $scope.password == "undefined"){
			tmpMessage = "* Password length must be more then 3 characters."
			if(message != ""){
				message += "\n" + tmpMessage;
			}else{
				message = tmpMessage;
			}
		}

		if(typeof $scope.nickname == "undefined"){
			tmpMessage = "* Please input the nickname what you want.\n   Nickname rule:\n       - 5~25 characters\n       - alphabets and numbers only.";
			if(message != ""){
				message += "\n" + tmpMessage;
			}else{
				message = tmpMessage;
			}
		}
		if(message != ""){
			alert(message);
		} else {
			$http({
				method: 'POST',
				url: 'checkDuplicateEmail',
				data: {
					email: $scope.email,
					password: $scope.password,
					nickname: $scope.nickname
				}
			}).then(
				function(response){
					console.log("result:"+response.data.result);
					if(response.data.result != "0"){
						$scope.chatStorage.tempPID = response.data.result;
						$('#vertifyModal').modal('open');
					}else{
						Materialize.toast('It\'s exist Email address. Try another.', 4000);
					}
				},
				function(response){
					alert("fail:"+response);
				}
			);
		}
	}
	
	$scope.checkVertifyNumber = function(){
		var message = "";
		if(typeof $scope.vertifyNumber == "undefined"){
			message = "Please input 4 digit vertify number.";
			alert(message);
		}else{
			$http({
				method: 'POST',
				url: 'checkVertifyNumber',
				data: {
					vnm: $scope.vertifyNumber,
					pid: $scope.chatStorage.tempPID
				}
			}).then(
				function(response){
					if(response.data.result == "1"){
						$scope.loadingData($scope.chatStorage.tempPID);
						$('#loginModal').modal('close');
						$('#vertifyModal').modal('close');
					}else{
						Materialize.toast('Vertify number is incorrect. Try again.', 4000);
					}
				},
				function(response){
					alert("fail:"+response);
				}
			);
		}
	}
	
	$scope.loadingData = function(pid){
		$http({
			method: 'POST',
			url: 'getUserInfo',
			data: {
				pid: pid
			}
		}).then(
			function(response){
				$scope.chatStorage.myStatus = response.data.myStatus;
				$scope.chatStorage.friendRequestList = response.data.friendRequestList;
				$scope.chatStorage.deletedFriendList = response.data.deletedFriendList;
				$scope.chatStorage.friendList = response.data.friendList;
				$scope.chatStorage.viewTab = 1;
				
				var data = {
					CODE:"A01",
					PID:pid
				};
				socket.send(data);
				
				var lastDate = new Date("2001-01-01");	//Before service
				var lastPid = 0;
				for(var i = 0; i < $scope.chatStorage.friendList.length; i++){
					var fObj = $scope.chatStorage.friendList[i];
					var obj = fObj.message;
					if(typeof obj != "undefined"){
						if(obj.length > 0){
							var chatDate = new Date(obj[obj.length - 1].datetime);
							if(lastDate < chatDate){
								lastPid = fObj.pid;
								lastDate = chatDate;
							}
						}
					}
				}
				$scope.chatStorage.showPID = lastPid;
			},
			function(response){
				alert("fail:"+response);
			}
		);
	}
	
	socket.onmessage(function(event) {
		var data = JSON.parse(event.data);
		var code = data.CODE;
		
		if(code == "C01") {			//User data
			$scope.sockProcSetFriendOnline(data.LIST);
		} else if(code == "C02"){	//Friend data
			$scope.addFriend(data.FRIEND);
		} else if(code == "B01") {	//Friend logon
			$scope.sockProcFriendLogon(data.PID);
			var idx = $scope.chatStorage.friendList.findIndex(function(item){
				return item.pid == data.PID;
			});
			
			if(idx > -1){
				var name = $scope.chatStorage.friendList[idx].name;
				Materialize.toast(name + ' has been login', 4000, 'green');
			}
		} else if(code == "B02") {	//Friend logoff
			$scope.sockProcFriendLogoff(data.PID);
		} else if(code == "B04") {	//Friend change status
			$scope.sockProcFriendStatusChange(data.PID, data.STATUS);
		} else if(code == "B05") {	//Friend change status message
			$scope.sockProcFriendStatusMessageChange(data.PID, data.MESSAGE);
		} else if(code == "B06") {	//Friend change portrait image
			$scope.sockProcFriendPortraitChange(data.PID, data.FILE);
		} else if(code == "B11") {	//Someone sent request to be a friend
			$scope.sockProcAddFriendRequest(data.REQUEST);
		} else if(code == "B12") {	//Someone sent cancel request to be a friend
			$scope.sockProcCancelFriendRequest(data.PID);
		} else if(code == "B20") {	//Get a chat message
			$scope.sockProcGetChatMessage(data);
		}
		$timeout(function(){}, 500);
    });
	
	$scope.sockProcGetChatMessage = function(data){
		var fPid = data.FPID;
		var message = data.MESSAGE;
		var chatSeq = data.CHATSEQ;	//FOR A CHECKING NEW MESSAGE OR NOT
		var read = 'N';
		if($scope.chatStorage.showPID == fPid && $scope.chatStorage.viewTab == 3){
			read = 'Y';				//FOR A CHECKING NEW MESSAGE OR NOT
		}
		
		var newMessage = {
			sender: '2',
			datetime:new Date(),
			read:read,
			message:message
		}
		
		var idx = $scope.chatStorage.friendList.findIndex(function(item){
			return item.pid == fPid;
		});
		$scope.chatStorage.friendList[idx].message.push(newMessage);
		
		if($scope.chatStorage.showPID == fPid){
			var chatObj = angular.element('.mc_friendChatBoard');
			$timeout(function(){}, 100);
			chatObj.animate({scrollTop :  $(".mc_friendChatBoard")[0].scrollHeight}, 400);
			if($scope.chatStorage.viewTab != 3){
				$scope.chatStorage.tab3New = true;
			} else {
				$scope.chatStorage.tab3New = false;
			}
		} else {
			if($scope.chatStorage.viewTab != 2){
				$scope.chatStorage.tab2New = true;
			}
		}
	}
	
	$scope.sockProcCancelFriendRequest = function(fPid){
		var idx = $scope.chatStorage.friendRequestList.findIndex(function(item){
			return item.pid == fPid;
		});
		if(idx > -1){
			$scope.chatStorage.friendRequestList.splice(idx,1);
		}
	}
	
	$scope.sockProcAddFriendRequest = function(request){
		$scope.chatStorage.friendRequestList.push(request);
	}
	
	$scope.addFriend = function(friend){
		$scope.chatStorage.friendList.push(friend);
	}
	
	$scope.sockProcFriendPortraitChange = function (fPid, portraitFile){
		var idx = $scope.chatStorage.friendList.findIndex(function(item){
			return item.pid == fPid;
		});
		if(idx > -1){
			$scope.chatStorage.friendList[idx].portraitFile = portraitFile;	
		}
	}
	
	$scope.sockProcSetFriendOnline = function(list){
		for(var i = 0; i < list.length; i++){
			var fPid = list[i].PID;
			$scope.sockProcFriendLogon(fPid);
		}
	}
	$scope.sockProcFriendLogoff = function(fPid){
		var idx = $scope.chatStorage.friendList.findIndex(function(item){
			return item.pid == fPid;
		});
		if(idx > -1){
			$scope.chatStorage.friendList[idx].online = true;	
		}
	}
	$scope.sockProcFriendStatusChange = function (fPid, status){
		var idx = $scope.chatStorage.friendList.findIndex(function(item){
			return item.pid == fPid;
		});
		if(idx > -1){
			$scope.chatStorage.friendList[idx].status = status;	
		}
	}
	$scope.sockProcFriendStatusMessageChange = function (fPid, message){
		var idx = $scope.chatStorage.friendList.findIndex(function(item){
			return item.pid == fPid;
		});
		if(idx > -1){
			$scope.chatStorage.friendList[idx].statusMessage = message;	
		}
	}
	
	$scope.sockProcFriendLogon = function(fPid){
		var idx = $scope.chatStorage.friendList.findIndex(function(item){
			return item.pid == fPid;
		});
		if(idx > -1){
			$scope.chatStorage.friendList[idx].online = true;
		}
	}
	
	$scope.sockProcFriendLogoff = function(fPid){
		var idx = $scope.chatStorage.friendList.findIndex(function(item){
			return item.pid == fPid;
		});
		if(idx > -1){
			$scope.chatStorage.friendList[idx].online = false;
		}
	}

	//Functions
	$scope.doBlock = function(pid){
		var idx = $scope.chatStorage.friendList.findIndex(function(item){
			return item.pid == pid;
		});
		
		if(idx > -1){
			$scope.chatStorage.friendList[idx].block = true;
		}
		
		var data = {
			CODE:"A07",
			PID:$scope.chatStorage.myStatus.pid,
			FID:pid
		}
		socket.send(data);
	}
	
	$scope.doUnblock = function(pid){
		var idx = $scope.chatStorage.friendList.findIndex(function(item){
			return item.pid == pid;
		});
		
		if(idx > -1){
			$scope.chatStorage.friendList[idx].block = false;
		}
		
		var data = {
			CODE:"A08",
			PID:$scope.chatStorage.myStatus.pid,
			FID:pid
		}
		socket.send(data);
		$timeout(function(){}, 50);	
	}
	
	$scope.getSendButtonClass = function(block){
		if(!block)return "waves-effect waves-light btn mc_resize06";
		else return "btn mc_btnDisable mc_resize06";
	}
	
	$scope.sendMessage = function(fpid, block, chatId){
		if(block)return;	//Do nothing when send message to blocked user
		else {
			message = angular.element('#mc_fcMessage').val();
			if(message != ""){
				var data = {
					CODE:"A20",
					PID:$scope.chatStorage.myStatus.pid,
					FPID:fpid,
					CHATID:chatId,
					MESSAGE:message
					
				}
				angular.element('#mc_fcMessage').val('');
				socket.send(data);
				
				var idx = $scope.chatStorage.friendList.findIndex(function(item){
					return item.pid == fpid;
				});
				
				var newMessage = {
					sender: '1',
					datetime:new Date(),
					read:'Y',
					message:message
				}
				$scope.chatStorage.friendList[idx].message.push(newMessage);
				var chatObj = angular.element('.mc_friendChatBoard');
				chatObj.animate({scrollTop :  $(".mc_friendChatBoard")[0].scrollHeight}, 400);
			}
		}
	}
	
	$scope.tabClick = function(tab){
		if(tab == 1){
			$scope.chatStorage.viewTab = 1;
		}else if(tab == 2){
			$scope.chatStorage.viewTab = 2;
			$scope.chatStorage.tab2New = false;
		}else if(tab == 3){
			$scope.chatStorage.viewTab = 3;
			$scope.chatStorage.tab3New = false;
			var idx = $scope.chatStorage.friendList.findIndex(function(item){
				return item.pid == $scope.chatStorage.showPID;
			});
			
			if(idx > -1){
				for(i in $scope.chatStorage.friendList[idx].message){
					$scope.chatStorage.friendList[idx].message[i].read = 'Y';
				}
			}
		}
	}
	
	$scope.chatStart = function(pid){
		$scope.chatStorage.showPID = pid;

		//Reason of using $timeout is breaking $digest cycle of angularJS
		$timeout(function(){
			//'tabs' method and 'select_tab' parameter are provided by Materialize 
			angular.element('ul.tabs').tabs('select_tab', 'FriendChat');
		}, 100);	

		var chatObj = angular.element('.mc_friendChatBoard');
		chatObj.animate({scrollTop :  $(".mc_friendChatBoard").length}, 400);
	}
	
	$scope.chatLeave = function(pid){
		var idx = $scope.chatStorage.friendList.findIndex(function(item){
			return item.pid == pid;
		});
		
		var newMessage = {
				sender: '3',
				datetime:Date.now(),
				message:'You left chat room'
		}
		
		if(idx > -1){
			$scope.chatStorage.friendList[idx].message = [];
		}
	}
	
	$scope.removeFriend = function(pid){
		$scope.chatStorage.removeFriendPid = pid;
		angular.element('#removeFriendModal').modal('open');
	}
	
	$scope.removeFriendRun = function(){
		var pid = $scope.chatStorage.removeFriendPid;
		var idx = $scope.chatStorage.friendList.findIndex(function(item){
			return item.pid == pid;
		});

		if(idx > -1){
			var deletedFriend = {
				pid : pid,
				name : $scope.chatStorage.friendList[idx].name,
				fDigit : $scope.chatStorage.friendList[idx].fDigit
			}
			
			$scope.chatStorage.friendList.splice(idx,1);
			
			var data = {
				CODE:"A09",
				PID:$scope.chatStorage.myStatus.pid,
				FID:pid
			}
			socket.send(data);
			
			$scope.chatStorage.deletedFriendList.push(deletedFriend);
			$timeout(function(){}, 50);	
		}
	}
	
	$scope.acceptFriendRequest = function(pid){
		var idx = $scope.chatStorage.friendRequestList.findIndex(function(item){
			return item.pid == pid;
		});
		if(idx > -1){
			$scope.chatStorage.friendRequestList.splice(idx,1);
			var data = {
				CODE:"A13",
				PID:$scope.chatStorage.myStatus.pid,
				FID:pid
			}
			socket.send(data);
		}
	}
	
	$scope.refuseFriendRequest = function(pid){
		var idx = $scope.chatStorage.friendRequestList.findIndex(function(item){
			return item.pid == pid;
		});
		if(idx > -1){
			$scope.chatStorage.friendRequestList.splice(idx,1);
			var data = {
				CODE:"A14",
				PID:$scope.chatStorage.myStatus.pid,
				FID:pid
			}
			socket.send(data);
		}
	}
	
	$scope.sendFriendRequest = function(pid){
		var idx = $scope.chatStorage.searchResult.findIndex(function(item){
			return item.pid == pid;
		});
		
		if(idx > -1){
			$scope.chatStorage.searchResult[idx].status = '1';
			var data = {
				CODE:"A11",
				PID:$scope.chatStorage.myStatus.pid,
				FID:pid
			}
			socket.send(data);
		}	
	}
	
	$scope.cancelFriendRequest = function(pid){
		var idx = $scope.chatStorage.searchResult.findIndex(function(item){
			return item.pid == pid;
		});
		
		if(idx > -1){
			$scope.chatStorage.searchResult[idx].status = '0';
			var data = {
				CODE:"A12",
				PID:$scope.chatStorage.myStatus.pid,
				FID:pid
			}
			socket.send(data);
		}
	}
	
	$scope.searchFriend = function(pageStartNumber, targetNumber){
		var sObc = angular.element('#searchKeyword');
		
		if(sObc.val().length < 3){
			alert("Must input at least 3 character");
		} else {
			$http({
				method: 'POST',
				url: 'searchFriend',
				data: {
					pid: $scope.chatStorage.myStatus.pid,
					keyword: sObc.val(),
					pageNumber: pageStartNumber,
					targetNumber: targetNumber
				}
			}).then(
				function(response){
					$scope.chatStorage.pageNumbers = response.data.pageNumbers;
					$scope.chatStorage.pagesOfResult = response.data.pagesOfResult;
					$scope.chatStorage.viewPage = response.data.viewPage;
					$scope.chatStorage.searchResult = response.data.searchResult;
					
					var obj = angular.element('#searchNoResult');
					if(response.data.pagesOfResult == "0"){
						obj.html('No result');
					} else {
						obj.html('');
					}
				},
				function(response){
					alert("fail:"+response);
				}
			);
		}
	}
	
	$scope.movePage = function(reqType, targetNumber){
		var pageStartNumber = 0;
		if(reqType == 'prev'){
			pageStartNumber = targetNumber - ((targetNumber - 1) % 5) - 5;
			targetNumber = pageStartNumber;
		}else if(reqType == 'next'){
			pageStartNumber = targetNumber - ((targetNumber - 1) % 5) + 5;
			targetNumber = pageStartNumber;
		}else if(reqType == 'move'){
			pageStartNumber = targetNumber - ((targetNumber - 1) % 5);
		}

		if(pageStartNumber > 0){
			$scope.searchFriend(pageStartNumber, targetNumber);
		}
	}
	
	$scope.restoreDeletedFriend = function(pid){
		var idx = $scope.chatStorage.deletedFriendList.findIndex(function(item){
			return item.pid == pid;
		});
		
		if(idx > -1){
			$scope.chatStorage.deletedFriendList.splice(idx,1);
		}
		
		var data = {
			CODE:"A10",
			PID:$scope.chatStorage.myStatus.pid,
			FID:pid
		}
		socket.send(data);
	}
});

/* SERVICE */
app.factory('socket', function(){
	var stack = [];
	var onmessageDefer;
	
	//WebSocket Uri on Amazon AWS instance
	//var wsUri = "ws://mychat.us-east-2.elasticbeanstalk.com/websocket/ws.do";
	
	//Localhost
	var wsUri = "ws://localhost:8080/myapp/websocket/ws.do";
	
	var socket = {
		ws: new WebSocket(wsUri),
		send: function(data){
			data = JSON.stringify(data);
			if(socket.ws.readyState == 1){
				socket.ws.send(data);
			} else {
				stack.push(data);
			}
		},
		onmessage: function(callback){
			if(socket.ws.readyState == 1){
				socket.ws.onmessage = callback;
			} else {
				onmessageDefer = callback;
			}
		}
	};
	socket.ws.onopen = function(event){
		for(i in stack){
			socket.ws.send(stack[i]);
		}
		stack = [];
		if(onmessageDefer){
			socket.ws.onmessage = onmessageDefer;
			onmessageDefer = null;
		}
	};
	return socket;
});

app.factory('storage', function(){
	var storage = {
		systemDefault:{
			userStatusForUser:[
				{
					code:"1",
					title:"Online"
				},
				{
					code:"2",
					title:"Away"
				},
				{
					code:"3",
					title:"Busy"
				},
			]
		},
		myStatus:{
			pid:'00000000',
			id:'00000000',
			name:' ',
			fDigit:' ',
			status:'1',
			statusMessage:'',
			portraitFile:''
		},
		deletedFriendList : [],
		friendRequestList : [],
		friendList : [{}],
		showPID:'00000000',
		removeFriendPid:'',
		tab1New:false,
		tab2New:false,
		tab3New:false,
		viewTab: 1,				//Tab number of activated tab

		get: function (){
			return storage;
		}
	}
	return storage;
});