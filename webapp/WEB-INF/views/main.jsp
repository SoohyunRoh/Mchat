<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<html lang="en" >
	<head>
		<meta charset="utf-8">
		<!--Import Google Icon Font-->
		<!--<link href="http://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">-->
		<link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
		<!--Import materialize.css (min)-->
		<link type="text/css" rel="stylesheet" href="css/materialize.min.css"  media="screen,projection"/>
		
		<!-- mychat css -->
		<link type="text/css" rel="stylesheet" href="css/mychat.css" rel="stylesheet">
		
		<!--Let browser know website is optimized for mobile-->
		<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
		
		<!--Import jQuery before materialize.js (min)-->
		<script type="text/javascript" src="https://code.jquery.com/jquery-2.1.1.min.js"></script>

		<!-- AngularJS 1.5.7 -->
		<script data-require="angular.js@1.5.7" data-semver="1.5.7" src="https://code.angularjs.org/1.5.7/angular.js"></script>
		
		<!-- AngularJS File upload 12.2.13 (min) and add API for IE 8, 9-->
		<script>
			window.FileAPI = {
			    jsUrl: 'https://cdnjs.cloudflare.com/ajax/libs/danialfarid-angular-file-upload/12.2.13/FileAPI.js',
			    flashUrl: 'https://cdnjs.cloudflare.com/ajax/libs/danialfarid-angular-file-upload/12.2.13/FileAPI.flash.swf',
			};
		</script>
		<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/danialfarid-angular-file-upload/12.2.13/ng-file-upload-shim.min.js"></script>
		
		<!-- AngularJS File upload 12.2.13 (min)-->
		<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/danialfarid-angular-file-upload/12.2.13/ng-file-upload.min.js"></script>
	</head>
    <body id="MyChatCtrl" ng-app="myChat" ng-controller="MyChatCtrl">
		<header>
			<div class="container">
				<div class="row mc_logo">				<!-- logo and login/logout -->
					<div class="col s6 ">
						<div class="valign-wrapper mc_logo">
							<img src="${imgUrl}/logo.jpg"/>
						</div>
					</div>
					<div class="col s6 ">
						<div class="valign-wrapper mc_logo right">
							<a href="#loginModal" ng-show="chatStorage.myStatus.pid == null" class="waves-effect waves-light btn mc_loginBtn">LOGIN</a>
							<a href="#" ng-click="logout();" ng-show="chatStorage.myStatus.pid != null" class="waves-effect waves-light btn mc_loginBtn">LOGOUT</a>
						</div>
					</div>
				</div>
			</div>
		</header>
		<form name="myChatForm">
			<div class="container">
				<div id="loginModal" class="modal">
					<div class="modal-content">
						<div class="row">
							<div class="col s12"><b>Login</b></div>
						</div>
						<div class="row">
							<div class="col s1"></div>
							<div class="col s10">
								<input id="email" type="email" class="mc_resize01" placeholder="Email" ng-model="email" required>
							</div>
							<div class="col s1"></div>
						</div>
						<div class="row">
							<div class="col s1"></div>
							<div class="col s10 ">
								<input id="password" type="password" class="mc_resize01" placeholder="Password" ng-model="password" ng-minlength="6" ng-Keypress="loginEnterKey($event)" required>
							</div>
							<div class="col s1"></div>
						</div>
						<div class = "row">
							<div class="col s1"></div>
							<div class="col s10">
								<input id="nickname" type="text" class="mc_resize01" placeholder="Nickname(Sign-up Only)" ng-model="nickname" ng-minlength="5" ng-maxlength="25" ng-pattern="/^[A-Za-z0-9]*$/">
							</div>
							
							<div class="col s1"></div>
						</div>					
						<div class="row">
							<div class="col s1"></div>
							<div class="col s5">
								<a class="waves-effect waves-light btn mc_loginModalBtn" ng-click="login()">SIGN IN</a>
							</div>
							<div class="col s5">
								<a class="waves-effect waves-light btn mc_loginModalBtn" ng-click="signUp()">SIGN UP</a>
							</div>
							<div class="col s1"></div>
						</div>
					</div>
				</div>
				<!-- Remove friend modal -->
				<div id="removeFriendModal" class="modal">
					<div class="modal-content">
						<div class="row">
							<div class="col s12"><b>Remove friend</b></div>
						</div>
						<div class="row">
							<div class="col s1"></div>
							<div class="col s10 ">
								<p>Are you sure to remove friend?</p>
							</div>
							<div class="col s1"></div>
						</div>
						<div class="row">
							<div class="col s1"></div>
							<div class="col s5">
								<a href="#!" class="modal-action modal-close waves-effect btn mc_loginModalBtn" ng-click="chatStorage.removeFriendPid = ''">Cancel</a>
							</div>
							<div class="col s5">
								<a href="#!" class="modal-action modal-close waves-effect btn mc_loginModalBtn" ng-click="removeFriendRun()">OK</a>
							</div>
							<div class="col s1"></div>
						</div>
					</div>
				</div>
				<div id="vertifyModal" class="modal">
					<div class="modal-content">
						<div class="row">
							<div class="col s12"><b>Sign Up</b>: E-mail that contains vertify number has been sent to your email address</div>
						</div>
						<div class="row">
							<div class="col s1"></div>
							<div class="col s10">
								<input id="vertifyNumber" type="text" class="mc_resize01" ng-model="vertifyNumber" placeholder="Vertify Number (4 digit)" ng-minlength="4" ng-maxlength="4" ng-pattern="/^[0-9]*$/">
							</div>
							<div class="col s1"></div>
						</div>						
						<div class="row">
							<div class="col s1"></div>
							<div class="col s10">
								<a class="waves-effect waves-light btn" ng-click="checkVertifyNumber()">SUBMIT</a>
							</div>
							<div class="col s1"></div>
						</div>
					</div>
				</div>
				<div class="row">
					<ul id="tabs" class="tabs">
						<li class="tab col s3" ng-click="tabClick(1)">
							<a class="tabsColor" href="#FriendList" >
								<div id="tab_FriendList">
									<div class="mc_tab_icon">
										<i ng-if="chatStorage.tab1New == true" class="tiny mc_noticeIcon material-icons">radio_button_checked</i>
										<i ng-if="chatStorage.tab1New == false" class="material-icons">people</i>
									</div>
									<div id="tab" class="mc_tab">Friend List</div>
								</div>
							</a>
						</li>
						<li class="tab col s3" ng-click="tabClick(2)">
							<a class="tabsColor" href="#ChatList">
								<div id="tab_ChatList">
									<div class="mc_tab_icon">
										<i ng-if="chatStorage.tab2New == true" class="tiny mc_noticeIcon material-icons">radio_button_checked</i>
										<i ng-if="chatStorage.tab2New == false" class="material-icons">toc</i>
									</div>
									<div id="tab" class="mc_tab">Chat List</div>
								</div>
							</a>
						</li>
						<li class="tab col s3" ng-click="tabClick(3)" id="tab_FriendChat">
							<a class="tabsColor" href="#FriendChat">
								<div >
									<div class="mc_tab_icon">
										<i ng-if="chatStorage.tab3New == true" class="tiny mc_noticeIcon material-icons">radio_button_checked</i>
										<i ng-if="chatStorage.tab3New == false" class="material-icons">compare_arrows</i>
									</div>
									<div id="tab" class="mc_tab">Friend Chat</div>
								</div>
							</a>
						</li>
					</ul>
					<div id="FriendList" class="col s12" ng-show="chatStorage.viewTab == 1">
						<div class="row mc_pinfo_top">
							<div class="col s2 mc_pinfo_top_img">
								<a href="#portraitChange">
									<img src="${imgUrl}/{{chatStorage.myStatus.portraitFile}}" onerror="this.src='${imgUrl}/noimg.jpg'">
								</a>
							</div>
							<div class="col s8 mc_pinfo_top_account mc_ex01" >
								{{chatStorage.myStatus.id}}
							</div>
							<div class="col s8 mc_pinfo_top_account" >
								<b>{{chatStorage.myStatus.name}}</b>#<i>{{chatStorage.myStatus.fDigit}}</i>
							</div>
						</div>
						<div class="row mc_ex01" >
							<div class="input-field col s3 mc_statusSel">
								<select name="userStatus" class="browser-default" ng-change="changeUserStatus()" ng-model="chatStorage.myStatus.status">
									<option ng-repeat="item in chatStorage.systemDefault.userStatusForUser"
											value="{{item.code}}"
											ng-selected="{{chatStorage.myStatus.status == item.code}}">
											{{item.title}}
									</option>
								</select>
							</div>
							<div class="col s9 valign-wrapper">
								<h6 class="mc_pinfo_msg">
									<input type="text" class="mc_input_resize01" value="{{chatStorage.myStatus.statusMessage}}" ng-blur="changeUserStatusMessage()" ng-model="chatStorage.myStatus.statusMessage" ng-maxlength="50">
								</h6>
							</div>
						</div>
						<div class="mc_flist_outline">
							<ul class="collapsible" data-collapsible="accordion">
								<!-- A ROW FOR A FRIEND -->
								<li ng-repeat="friend in chatStorage.friendList">
									<div class="collapsible-header">
										<div class="row mc_flist">	
											<div class="col s2 mc_flist_img">
												<img src="${imgUrl}/{{friend.portraitFile}}" onerror="this.src='${imgUrl}/noimg.jpg'"> 
											</div>
											<div class="col s10">
												<div class="row mc_resize04">
													<div class="col s1 mc_right" ng-switch="friend.online">
														<div ng-switch-when="true" ng-switch="friend.status">
															<div ng-switch="friend.block">
																<div ng-switch-when="false" ng-switch="friend.status">														
																	<i ng-switch-when="1" class="material-icons mc_resize05 mc_online">perm_identity</i>
																	<i ng-switch-when="2" class="material-icons mc_resize05 mc_busy">motorcycle</i>
																	<i ng-switch-when="3" class="material-icons mc_resize05 mc_away">query_builder</i>
																</div>
																<div ng-switch-when="true">
																	<i class="material-icons mc_resize05 mc_block">block</i>
																</div>
															</div>
														</div>
														<div ng-switch-when="false" ng-switch="friend.status">
															<div ng-switch="friend.block">
																<div ng-switch-when="false" ng-switch="friend.status">
																	<i ng-switch-when="1" class="material-icons mc_resize05 mc_offline">perm_identity</i>
																	<i ng-switch-when="2" class="material-icons mc_resize05 mc_offline">motorcycle</i>
																	<i ng-switch-when="3" class="material-icons mc_resize05 mc_offline">query_builder</i>
																</div>
																<div ng-switch-when="true">
																	<i class="material-icons mc_resize05 mc_offline">block</i>
																</div>
															</div>
														</div>
													</div>
													<div class="col s11 mc_nowrap mc_friendname">
														<b>{{friend.name}}</b>#
														<span class="mc_i">{{friend.fDigit}}</span>
													</div>
												</div>
												<div class="row mc_resize04">
													<div class="col s12 mc_i">
														{{friend.statusMessage}}
													</div>
												</div>
											</div>
										</div>
									</div>
									<div class="collapsible-body valign-wrapper">
										<div class="row">
											<div class="col s3 mc_right" ng-switch="friend.block">
												<a class="waves-effect waves-light btn mc_warning" ng-switch-when="false" ng-click="doBlock(friend.pid)">
													Block
												</a>
												<a class="waves-effect waves-light btn mc_unblock" ng-switch-when="true" ng-click="doUnblock(friend.pid)">
													Unblock
												</a>
											</div>
											<div class="col s3 ">
												<a class="waves-effect waves-light btn mc_delete" ng-click="removeFriend(friend.pid)">
													Remove
												</a>		
											</div>
											<div class="col s6">
												<a class="waves-effect waves-light btn" ng-if="friend.block == false && friend.online == true" ng-click="chatStart(friend.pid)">
													Chat start
												</a>
												<a class="waves-effect waves-light btn disabled" ng-if="friend.block == true || friend.online == false">
													Chat start
												</a>	
											</div>
										</div>
									</div>								
								</li>
							</ul>
						</div>
						<div class="row mc_addFriendtitle" ng-show="chatStorage.friendRequestList.length > 0">
							<div class="col s12">
								Add friend request
							</div>
						</div>
						<div class="mc_reqFriend" ng-show="chatStorage.friendRequestList.length > 0">
							<ul class="collection">
								<li class="collection-item" ng-repeat="friendRequest in chatStorage.friendRequestList">
									<div>
										{{friendRequest.name}}# 
										{{friendRequest.fDigit}}
										<a href="#!" class="secondary-content">
											<div>
												<i class="material-icons" ng-click="acceptFriendRequest(friendRequest.pid)">done</i>
												&nbsp;&nbsp;
												<i class="material-icons" ng-click="refuseFriendRequest(friendRequest.pid)">clear</i>
											</div>
										</a>
									</div>
								</li>
							</ul>
						</div>
						<div class="mc_addFriend">
							<a class="waves-effect waves-light btn" href="#searchFriend">
								<i class="material-icons left">add</i>Add Friend
							</a>
						</div>
						<div class="mc_deletedFriendTitle" ng-show="chatStorage.deletedFriendList.length > 0">
							<a class="waves-effect waves-light btn mc_deletedFriendListBtn" href="#deletedFriend">
								Show deleted friend list
							</a>
						</div>
						<div id="portraitChange" class="modal">
							<div class="modal-content">
								<div class="row">
									<div class="col s12"><b>Change my Portrait</b></div>
								</div>
								<div class="row">
									<div class="card">
										<div class="card-image">
											<img ng-show="file == null" src="${imgUrl}/{{chatStorage.myStatus.portraitFile}}" onerror="this.src='${imgUrl}/noimg.jpg'">	
											<img ng-show="file != null" ngf-src="file">
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col s12 valign-wrapper" >
										<div class="waves-effect waves-light btn" ng-model="file" name="file" ngf-select ngf-pattern="'image/*'" ngf-accept="'image/*'" ngf-max-size="10MB">Select Image</div>
									</div>
								</div>
								<div class="row">
									<div class="col s12" >
										<a class="waves-effect waves-light btn" ng-click="fileUpload()">
											<i class="material-icons left" >backup</i>UPLOAD <span ng-bind="uploadPercent"></span>
										</a>
									</div>
								</div>
							</div>
						</div>
						<div id="searchFriend" class="modal">
							<div class="modal-content">
								<div class="row">
									<div class="col s12"><b>Search a friend</b></div>
								</div>
								<div class="row">
									<div class="col s10">
										<input type="text" id="searchKeyword" class="mc_resize01" placeholder="email or nickname">
									</div>
									<div class="col s2 valign-wrapper mc_resize01" ng-click="searchFriend(1, 1)">
										<i class="material-icons">search</i>
									</div>
								</div>
								<div class="row">
									<div id="searchNoResult"></div>
									<ul class="collection">
										<li class="collection-item mc_resize02" ng-repeat="searchResult in chatStorage.searchResult">
											<div class="row mc_resize03 valign-wrapper">
												<div class="col s10 mc_resize03"><B>{{searchResult.name}}</b>#<i>{{searchResult.fDigit}}</i></div>
												<div class="col s2 mc_resize03 valign-wrapper" ng-if="searchResult.status == 0" ng-click="sendFriendRequest(searchResult.pid)">
													<i class="material-icons  mc_icon_border">add</i>
												</div>
												<div class="col s2 mc_resize03 valign-wrapper" ng-if="searchResult.status == 1" ng-click="cancelFriendRequest(searchResult.pid)">
													<i class="material-icons  mc_icon_border">cancel</i>
												</div>

											</div>
										</li>
									</ul>
									<div "col s12">
										<ul class="pagination">
											<!-- <li class="disabled" ng-click="movePage('prev',chatStorage.viewPage)"> -->
											<li class="waves-effect" ng-click="movePage('prev',chatStorage.viewPage)">
												<a href="#!" class="valign-wrapper">
													<i class="material-icons">chevron_left</i>
												</a>
											</li>
											<li ng-repeat="numbers in chatStorage.pageNumbers">
												<div ng-switch="chatStorage.viewPage == numbers.number">
													<a ng-switch-when="true" href="#!" class="mc_active">{{numbers.number}}</a>
													<a ng-switch-when="false" href="#!" class="waves-effect" ng-click="movePage('move',numbers.number)">{{numbers.number}}</a>
												</div>
											</li>
											<li class="waves-effect" ng-click="movePage('next',chatStorage.viewPage)">
												<a href="#!" class="valign-wrapper">
													<i class="material-icons">chevron_right</i>
												</a>
											</li>
										</ul>
									</div>
								</div>
							</div>
						</div>
						<div id="deletedFriend" class="modal">
							<div class="modal-content">
								<div class="row">
									<div class="col s12"><b>Deleted Friend List</b></div>
								</div>
								<div class="row">
									<ul class="collection">
										<li class="collection-item mc_resize02" ng-repeat="deletedFriend in chatStorage.deletedFriendList">
											<div class="row mc_resize03 valign-wrapper">
												<div class="col s10 mc_resize03"><B>{{deletedFriend.name}}</b>#<i>{{deletedFriend.fDigit}}</i></div>
												<div class="col s2 mc_resize03 valign-wrapper" ng-click="restoreDeletedFriend(deletedFriend.pid)">
													<i class="material-icons  mc_icon_border">add</i>
												</div>
											</div>
										</li>
									</ul>
								</div>
							</div>
						</div>
					</div>	
					<div id="ChatList" class="col s12 mc_FriendChatTitle" ng-show="chatStorage.viewTab == 2">
						<div class="mc_clist_outline">
							<ul class="collapsible" data-collapsible="accordion">
								<!-- A ROW FOR A FRIEND -->
								<li ng-repeat="personalMessages in chatStorage.friendList | filter:messageNotEmpty">
									<div class="collapsible-header">
										<div class="row mc_chatlist" >	
											<div class="col s2 mc_flist_img">
												<img src="${imgUrl}/{{personalMessages.portraitFile}}">
											</div>
											<div class="col s10">
												<div class="row mc_resize04">
													<div class="col s10 mc_nowrap" >
														<b>{{personalMessages.name}}#</b>
														<span class="mc_i">{{personalMessages.fDigit}}</span>
													</div>
													<div class="col s2 mc_right" ng-switch="personalMessages.online">
														<div ng-switch-when="true" ng-switch="personalMessages.status">
															<div ng-switch="personalMessages.block">
																<div ng-switch-when="false" ng-switch="personalMessages.status">														
																	<i ng-switch-when="1" class="material-icons mc_resize05 mc_online">perm_identity</i>
																	<i ng-switch-when="2" class="material-icons mc_resize05 mc_busy">motorcycle</i>
																	<i ng-switch-when="3" class="material-icons mc_resize05 mc_away">query_builder</i>
																</div>
																<div ng-switch-when="true">
																	<i class="material-icons mc_resize05 mc_block">block</i>
																</div>
															</div>
														</div>
														<div ng-switch-when="false" ng-switch="personalMessages.status">
															<div ng-switch="personalMessages.block">
																<div ng-switch-when="false" ng-switch="personalMessages.status">
																	<i ng-switch-when="1" class="material-icons mc_resize05 mc_offline">perm_identity</i>
																	<i ng-switch-when="2" class="material-icons mc_resize05 mc_offline">motorcycle</i>
																	<i ng-switch-when="3" class="material-icons mc_resize05 mc_offline">query_builder</i>
																</div>
																<div ng-switch-when="true">
																	<i class="material-icons mc_resize05 mc_offline">block</i>
																</div>
															</div>
														</div>
													</div>
												</div>
												<div class="row mc_resize04">
													<div class="col s11 mc_lastchat mc_s11" ng-if="personalMessages.message[personalMessages.message.length - 1].sender == '1'">
															Rcv:{{personalMessages.message[personalMessages.message.length - 1].message | limitTo:30 }}{{personalMessages.message[personalMessages.message.length - 1].message.length > 30 ? '...' : ''}}
													</div>
													<div class="col s11 mc_lastchat mc_s11" ng-if="personalMessages.message[personalMessages.message.length - 1].sender == '2'">
															Snd:{{personalMessages.message[personalMessages.message.length - 1].message | limitTo:30 }}{{personalMessages.message[personalMessages.message.length - 1].message.length > 30 ? '...' : ''}}
													</div>
													<div class="col s11 mc_lastchat mc_s11" ng-if="personalMessages.message[personalMessages.message.length - 1].sender == '3'">
															Sys:{{personalMessages.message[personalMessages.message.length - 1].message | limitTo:30 }}{{personalMessages.message[personalMessages.message.length - 1].message.length > 30 ? '...' : ''}}
													</div>
													<div class="col s1 mc_noticeIcon mc_s1" ng-if="hasNewMessage(personalMessages.pid) == true">New&nbsp</div>
												</div>
											</div>
										</div>
									</div>
									<div class="collapsible-body valign-wrapper">
										<div class="row">
											<div class="col s3 mc_right">
											</div>
											<div class="col s3 mc_right">
												<a class="waves-effect waves-light btn mc_warning" ng-click="chatLeave(personalMessages.pid)">
													Leave
												</a>	
											</div>
											<div class="col s6" ng-click="chatStart(personalMessages.pid)">
												<a class="waves-effect waves-light btn">
													Continue / View chat
												</a>		
											</div>
										</div>
									</div>
								</li>
							</ul>
						</div>
					</div>
					<div id="FriendChat" class="col s12 mc_FriendChatTitle" ng-show="chatStorage.viewTab == 3" ng-repeat="personalMessages in chatStorage.friendList | filter:showPidOnly">
						<div class="row mc_resize01">
							<div class="col s12 ">
								<b>{{personalMessages.name}}#</b>
								<span class="mc_i">{{personalMessages.fDigit}}</span>
							</div>
						</div>
						<div class="mc_friendChatBoard" >
							<div class="row mc_chatLine" ng-repeat="message in personalMessages.message">
								<div ng-switch="message.sender">
									<div ng-switch-when="2">
										<div class="col s2 mc_chatLine_Img_Left">
											<img src="${imgUrl}/{{personalMessages.portraitFile}}">
										</div>
										<div class="col s9 mc_chatLine_msg">
											<div class="row mc_chatLine_msg " >
												<div class="col s10 mc_chatLine_msg01">
													<div class="mc_inline" ng-if="personalMessages.block == true">
														{{personalMessages.name+"#"+personalMessages.fDigit}}(Block)
													</div>
													<div class="mc_inline" ng-if="personalMessages.block == false && personalMessages.online == false">
														{{personalMessages.name+"#"+personalMessages.fDigit}}(Offline)
													</div>
													<div class="mc_inline" ng-if="personalMessages.block == false && personalMessages.online == true">
														<div ng-switch="personalMessages.status" class="mc_inline">
															<div ng-switch-when="1" class="mc_inline">
																{{personalMessages.name+"#"+personalMessages.fDigit}}(online)
															</div>
															<div ng-switch-when="2" class="mc_inline">
																{{personalMessages.name+"#"+personalMessages.fDigit}}(away)
															</div>
															<div ng-switch-when="3" class="mc_inline">
																{{personalMessages.name+"#"+personalMessages.fDigit}}(busy)
															</div>
														</div>
													</div>
												</div>
												<div class="col s2 mc_chatLine_time">
													{{message.datetime | date : "ah:mm"}}
												</div>
											</div>
											<div class="row mc_chatLine_msg">
												<div class="col s12 mc_chatLine_msg02">
													<div class="mc_chatBubble">
														<span>{{message.message}}</span>
													</div>
												</div>
											</div>
										</div>
									</div>
									<div ng-switch-when="1">
										<div class="col s11 mc_chatLine_msg">
											<div class="row mc_chatLine_msg">
												<div class="col s12 mc_chatLine_time">
													{{message.datetime | date : "ah:mm"}}
												</div>
											</div>
											<div class="row mc_chatLine_msg">
												<div class="col s12 mc_chatLine_msg02 mc_yourMsg">
													<div class="mc_chatBubble mc_yourMsg mc_yourMsgColor">
														<span>{{message.message}}</span>
													</div>
												</div>
											</div>
										</div>
									</div>
									<div ng-switch-when="3">
										<div class="col s11 mc_chatLine_msg mc_systemMessage">
											<div class="row mc_chatLine_msg">
												<span>{{message.message}}</span>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="mc_friendChatBoard_Input">
							<div class="row">
								<div class="col s9 valign-wrapper" >
									<textarea id="mc_fcMessage" ng-disabled="personalMessages.block == true || personalMessages.online == false"></textarea>
								</div>
								<div class="col s3">
									<a id="mc_fcSend" ng-click="sendMessage(personalMessages.pid, personalMessages.block, personalMessages.chatId)" ng-class="getSendButtonClass(personalMessages.block)">
										Send
									</a>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</form>
		<footer class="page-footer">
			<div class="container">
            	<div class="row">
              		<div class="col l6 s12">
                	<div class="grey-text text-lighten-4">You can use rows and columns here to organize your footer content.</div>
              	</div>
            </div>
          	<div class="footer-copyright">
            	<div class="container">
             		<a href="http://www.apache.org/licenses/LICENSE-2.0.txt" target="_blank" class="mc_link">Apache License Version 2.0</a>
            		<a class="grey-text text-lighten-4 right" href="#!">More Links</a>
            	</div>
          	</div>
        </footer>

		<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/materialize/0.98.2/js/materialize.min.js"></script>
		<script>
			$(document).ready(function(){
				$('ul.tabs').tabs();
			});
			$(document).ready(function() {
				$('select').material_select();
			});
			$(document).ready(function(){
			// the "href" attribute of .modal-trigger must specify the modal ID that wants to be triggered
				$('.modal').modal();
			});
			


			$(document).ready(function(){
				
				$('#loginModal').modal({
					dismissible: false,	// Modal can be dismissed by clicking outside of the modal
					opacity: 1
				});
				
				$('#vertifyModal').modal({
					dismissible: false,	// Modal can be dismissed by clicking outside of the modal
					opacity: 1
				});
				
				<%
				if(session.getAttribute("pid") != null && !session.getAttribute("pid").equals("")){
				%>
				angular.element('#MyChatCtrl').scope().loadingData('<%=session.getAttribute("pid")%>');
				<%
				} else {
				%>
					$('#loginModal').modal('open');
				<%
				}
				%>
			});
			
			$('.modal').modal({
				complete: function() { alert('Closed'); }
			});
		</script>
		<script src="js/controller.js"></script>
    </body>
</html>