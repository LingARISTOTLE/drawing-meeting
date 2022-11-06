<template>
  <div>
    <button @click="open" class="but" >创建画板</button><br />
    <input type="text" v-model="createNumber" /><br>

    <button @click="join" class="but">加入画板</button><br>
    <input type="text" v-model="joinNumber" />
  </div>
</template>

<script>
import axios from "axios";
export default {
  name: "OpenOrJoin",
  data() {
    return {
      joinNumber: "",
      createNumber:''
    };
  },
  mounted() {
    console.log(this.$route.query.username);
  },
  methods: {
    open() {
        if (this.createNumber === "") {
        alert("请输入id");
        return;
      }
        axios.get(`http://10.224.202.17:8080/isRoomExist/${this.createNumber} `).then(
        (res) => {
          console.log(res.data.result);
          if (!res.data.result) {
           this.$router.replace({
              name: "drawingboard",
              query: {
                joinNumber: this.createNumber,
              },
            });
          } else {
            alert("房间号重复");
          }
        },)
      // this.$router.push("/drawing");

      
    },
    join() {
      
      if (this.joinNumber === "") {
        alert("请输入id");
        return;
      }
      //发送请求

      axios.get(`http://10.224.202.17:8080/isRoomExist/${this.joinNumber} `).then(
        (res) => {
          console.log(res.data.result);
          if (res.data.result) {
            this.$router.replace({
              name: "drawingboard",
              query: {
                joinNumber: this.joinNumber,
              },
            });
          } else {
            alert("房间不存在");
          }
        },
        (err) => {
          console.log(err);
        }
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

