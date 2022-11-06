<template>
  <div >
    <button @click="open" class="but">打开画板</button><br>
    <button @click="join" class="but">加入画板</button>
    <input type="text" v-model="joinNumber">
  </div>
</template>

<script>
// import axios from "axios";
export default {
  name: "OpenOrJoin",
  data() {
    return {
        joinNumber:''
    }
  },
  methods: {
    open() {
    this.$router.push('/drawing')
    }, join() {
//发送请求

        this.$router.push({
					name:'drawingboard',
					query:{	    
					joinNumber:this.joinNumber
					}
				})
    
    },
  },
};
</script>

<style>
.but{
    margin-bottom: 20px;
}
</style>