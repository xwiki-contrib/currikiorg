//If the console object is not defined this lets crash IE9 
		//javascript engine, and no further script is evaluated
try{
  console
}catch(e){
   console={}; console.log = function(){};
}