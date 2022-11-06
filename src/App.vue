<template>
  
  

    <router-view></router-view>
    
 
</template>

<script>

// import Login from './components/Login.vue'
export default {
  name: "App",
  components:{  }
};
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
*::before {
  box-sizing: border-box;
}
*::after {
  box-sizing: border-box;
}
ul {
  padding: 0;
}
li {
  list-style: none;
}
.clearfix::after {
  content: "";
  display: block;
  clear: both;
}
</style>
