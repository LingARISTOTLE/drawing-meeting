<template>
  <div class="login">
    <input type="text" placeholder="请输入用户名" v-model="username" /><br />
    <input type="password" placeholder="请输入密码" v-model="password" /><br />
    <button id="enter" @click="Login">登录</button>
    <button  @click="Register" >注册</button>
   
  </div>
</template>

<script>
import axios from "axios";

export default {
    name:'Login',
  data() {
    return {
      username: "",
      password: "",
    };
  },
  methods: {
    Login() {

// if(this.username.length<5){
//     alert('账号应大于5')
//     return
// }

console.log(this.$router);
      axios
        .get(
          `http://10.224.25.178:8080/login/${this.username}/${this.password} `
        )
        .then(res => {
            console.log(res.data.result);
          if (res.data.result) {
              localStorage.setItem("token", JSON.stringify(res.data[0]));
            this.$router.push("openorjoin")
          } else {
            alert(res.data.context);
          
          }
        });
    },Register(){
       axios
        .get(
          `http://10.224.25.178:8080/login/${this.username}/${this.password} `
        )
        .then(res => {
            console.log(res.data.result);
          if (res.data.result) {
              localStorage.setItem("token", JSON.stringify(res.data[0]));
            this.$router.push("openorjoin")
          } else {
            alert(res.data.context);
          
          }
        });
    }
  },
};
</script>

<style lang="css" scoped>
/* .login{
    position: ;
} */
</style>>

