<template>
	<!-- Referenced from https://freefrontend.com/css-dividers/ -->
	<div class="hr anim"></div>
</template>

<script>
</script>

<style>
	@keyframes bar {
	  	  0% { background-position: 0%; }
	  	  100% { background-position: 200%; }
	}
	  
	.hr {
	  width: 100%;
	  height: 1upx;
	  display: block;
	  position: relative;
	  margin-bottom: 10upx;
	  padding: 5upx 0;
	}
	
	.hr::before {
	  content: "";
	  position: absolute;
	
	  width: 100%;
	  height: 1upx;
	  bottom: 50%;
	  left: 0;
	
	  /* background: linear-gradient( 90deg, #10111F 0%, #10111F 50%, transparent 50%, transparent 100% ); */
	  background: linear-gradient( 90deg, #FFFFFF 0%, #FFFFFF 50%, transparent 50%, transparent 100% );
	  background-size: 15px;
	  background-position: center;
	  z-index: 1;
	}
	
	.hr::after {
	  content: "";
	  position: absolute;
	
	  width: 100%;
	  height: 1px;
	  bottom: 50%;
	  left: 0;
	
	  transition: opacity 0.3s ease, animation 0.3s ease;
	
	  background: linear-gradient(
		  to right, 
		  #62efab 5%, 
		  #F2EA7D 15%, 
		  #F2EA7D 25%, 
		  #FF8797 35%, 
		  #FF8797 45%, 
		  #e1a4f4 55%, 
		  #e1a4f4 65%, 
		  #82fff4 75%, 
		  #82fff4 85%, 
		  #62efab 95%);
	
	  background-size: 200%;
	  background-position: 0%;
	  -webkit-animation: bar 15s linear infinite;
	}
	
	.anim::before {
	  background: linear-gradient( 
			90deg, 
			#FFFFFF 0%, #FFFFFF 5%, 
			transparent 5%, transparent 10%, 
			#FFFFFF 10%, #FFFFFF 15%, 
			transparent 15%, transparent 20%, 
			#FFFFFF 20%, #FFFFFF 25%,
			transparent 25%, transparent 30%,
			#FFFFFF 30%, #FFFFFF 35%, 
			transparent 35%, transparent 40%, 
			#FFFFFF 40%, #FFFFFF 45%, 
			transparent 45%, transparent 50%, 
			#FFFFFF 50%, #FFFFFF 55%,
			transparent 55%, transparent 60%,
			#FFFFFF 60%, #FFFFFF 65%,
			transparent 65%, transparent 70%, 
			#FFFFFF 70%, #FFFFFF 75%, 
			transparent 75%, transparent 80%, 
			#FFFFFF 80%, #FFFFFF 85%,
			transparent 85%, transparent 90%,
			#FFFFFF 90%, #FFFFFF 95%, 
			transparent 95%, transparent 100% );
	
	  background-size: 150px;
	  background-position: center;
	  z-index: 1;
	
	  animation: bar 120s linear infinite;
	}
	
	.anim:hover::before {
	  animation-duration: 20s;
	}
	
	.anim:hover::after {
	  animation-duration: 2s;
	}
</style>
