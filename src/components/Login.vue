<template>
  <!-- <div class="login">
    <input type="text" placeholder="请输入用户名" v-model="username" /><br />
    <input type="password" placeholder="请输入密码" v-model="password" /><br />
    <button id="enter" @click="Login">登录</button>

    <button @click="Register">注册</button>
    <el-input placeholder="请输入密码" v-model="input" show-password></el-input>
  </div> -->

  <div id="wrapper">
    <div class="login">
      <div class="login-item">welcome to DrawingMeeting</div>

      <el-input
     v-model="username" 
        placeholder="请输入内容"
        class="login-item"
      ></el-input>

      <el-input
        placeholder="请输入密码"
     v-model="password"
        show-password
        class="login-item"
      ></el-input>
      <div class="btn">
        <el-button type="success" plain @click="Login">登录</el-button>
        <el-button type="primary" plain @click="Register">注册</el-button>
      </div>
    </div>
  </div>
</template>

<script>
import "element-ui/lib/theme-chalk/index.css";
import axios from "axios";

export default {
  name: "Login",
  data() {
    return {
      username: "",
      password: "",
    };
  },
  methods: {
    Login() {
      if (this.username.length === 0 || this.password.length === 0) {
        alert("账号密码不应为空");
        return;
      }

      console.log(this.$router);
      axios
        .get(
          `http://10.224.202.17:8080/login/${this.username}/${this.password} `
        )
        .then(
          (res) => {
            console.log(res.data.result);
            if (res.data.result) {
              localStorage.setItem("token", JSON.stringify(res.data.result));
              localStorage.setItem("username", this.username);

              this.$router.push("openorjoin");
              // this.$router.push({
              //   name: "openorjoin",

              // });
            } else {
              alert(res.data.context);
            }
          },
          (err) => {
            console.log(err);
          }
        );
    },
    Register() {
      axios
        .get(
          `http://10.224.202.17:8080/login/${this.username}/${this.password} `
        )
        .then(
          (res) => {
            console.log(res.data.result, "成功");
            if (res.data.result) {
              localStorage.setItem("token", JSON.stringify(res.data[0]));
              this.$router.push("openorjoin");
            } else {
              alert(res.data.context);
            }
          },
          (err) => console.log(err)
        );
    },
  },
};
</script>

<style lang="css" scoped>
.login {
  width: 300px;
  height: 100px;
}
.login-item {
  margin-bottom: 30px;
}
.btn {
  display: flex;
  justify-content: space-around;
}
body {
  margin: 0;
  padding: 0;
}
#wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100vw;
  height: 80vh;
  /* background-color: #999; */
}
</style>>

