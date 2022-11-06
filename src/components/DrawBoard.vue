<template>
  <div class="container">
    <canvas id="drawingBorad"></canvas>
    <div class="tool-container">
      <div class="icon-div icon" @click="isShowDrawPane = !isShowDrawPane">
        <icon name="draw" scale="4"></icon>
      </div>
      <div class="icon-div icon" @click="filterObject('erase')">
        <icon name="erase" scale="4"></icon>
      </div>
      <div class="icon-div icon" @click="filterObject('line')">
        <icon name="line" scale="4"></icon>
      </div>
      <div class="icon-div icon" @click="filterObject('arrows')">
        <icon name="arrows" scale="4"></icon>
      </div>
      <div class="icon-div icon" @click="filterObject('rect')">
        <icon name="rect" scale="4"></icon>
      </div>
      <div class="icon-div icon" @click="filterObject('circle')">
        <icon name="circle" scale="4"></icon>
      </div>
      <div class="icon-div icon" @click="filterObject('text')">
        <icon name="text" scale="4"></icon>
      </div>
      <div class="icon-div icon" @click="clearCanvas()">
        <icon name="clear" scale="4"></icon>
      </div>
      <div class="icon-div icon" @click="redo()">
        <icon
          :name="historyImageData.length > 0 ? 'redo' : 'grey-redo'"
          scale="4"
        ></icon>
      </div>
      <div class="icon-div icon" @click="cancelRedo()">
        <icon
          :name="
            newHistoryImageData.length > 0 ? 'cancelRedo' : 'grey-cancelRedo'
          "
          scale="4"
        ></icon>
      </div>
      <div class="icon-div icon" @click="downLoad()">
        <icon name="download" scale="4"></icon>
      </div>
      <div class="drawPane" v-show="isShowDrawPane">
        <div @click="isShowDrawPane = false">
          <icon class="close-draw-pane icon" name="close" scale="3"></icon>
        </div>
        <h5>画笔大小</h5>
        <input type="range" id="lwRange" min="1" max="10" value="1" />
        <h5>画笔颜色</h5>
        <input type="color" id="lcolor" v-model="temp" />
      </div>
    </div>
    <textarea
      id="textarea"
      name="textBox"
      cols="9"
      rows="1"
      class="text-style"
      v-show="isShowText"
    ></textarea>
  </div>
</template>
<script>
export default {
  name: "DrawBoard",
  props: {
    msg: String,
  },
  data() {
    return {
      temp: "black",
      isShowDrawPane: false,
      canvas: null,
      context: null,
      //线宽
      lwidth: 2,
      //画笔颜色
      lcolor: "orange",
      //维护绘画状态的数组
      paintTypeArr: {
        painting: false,
        erase: false,
        line: false,
        arrows: false,
        rect: false,
        circle: false,
        text: false,
      },
      //最近一次的canvas图片的数据
      imageData: null,
      //是否显示文字编写框
      isShowText: false,
      //保存画布图片历史的数据
      historyImageData: [],
      //保存已被撤销的历史画布图片数据
      newHistoryImageData: [],
      // ws: null,
      dataimg: "",
    };
  },
  watch: {
    imageData() {
      console.log(this.imageData);
    },
    historyImageData() {
      console.log(this.historyImageData);
    },
  },
  mounted() {
    console.log(this.$route.query.joinNumber);
    let self = this;
    self.init();
    this.wsinit();

    window.onresize = function () {
      self.init();
    };
    document.getElementById("lwRange").onchange = function () {
      self.lwidth = parseInt(document.getElementById("lwRange").value);
    };
    document.getElementById("lcolor").onchange = function () {
      self.context.fillStyle = document.getElementById("lcolor").value;
      self.context.strokeStyle = document.getElementById("lcolor").value;
    };
    this.listen();
    // this.initWebSocket()
  },
  beforeDestroy() {
    this.ws.close()
  },
  methods: {
    wsinit() {
      this.bindEvent();
    },
    bindEvent() {
      this.ws = new WebSocket("ws:localhost:8000");
      // oSendBtn.addEventListener("click", handleSendBtnClick, false)
      this.ws.addEventListener("open", this.handleOpen, false);
      this.ws.addEventListener("close", this.handleClose, false);
      this.ws.addEventListener("error", this.handleError, false);
      this.ws.addEventListener("message", this.handleMessage, false);
    },
    handleSend(a) {
      console.log(this);
      console.log("send message", a);
      this.ws.send(JSON.stringify(a));
    },
    handleOpen(e) {
      console.log("websocket open", e);
    },
    handleClose(e) {
      console.log("websocket close", e);
    },
    handleError(e) {
      console.log("websocket error", e);
    },
    handleMessage(e) {
      console.log(this);
      console.log("websocket message", e);
      console.log(e.data);

      let self = this;
      //转换步骤
      const file = new FileReader();
      file.readAsText(e.data, "utf-8");
      file.onload = function () {
        // console.log(file.result);
        const message = JSON.parse(file.result);
        // ctx.clearRect(0,0,canvas.width,canvas.height);
        // console.log(message);
        // 	historyData.push(message);
        self.historyImageData = message;
        // console.log(this);
        self.context.clearRect(0, 0, self.canvas.width, self.canvas.height);
        self.redraw(self.historyImageData[self.historyImageData.length - 1]);
      };
    },
    //初始化画布
    init() {
      this.canvas = document.getElementById("drawingBorad");
      this.context = this.canvas.getContext("2d");
      this.canvas.width = window.innerWidth;
      this.canvas.height = window.innerHeight;
      this.imageData && this.context.putImageData(this.imageData, 0, 0);
    },
    //监听鼠标,用于画笔任意绘制和橡皮擦
    listen() {
      let self = this;
      let lastPoint = { x: undefined, y: undefined };
      let rect = self.canvas.getBoundingClientRect();
      var scaleX = self.canvas.width / rect.width;
      var scaleY = self.canvas.height / rect.height;
      let textPoint = { x: undefined, y: undefined };

      self.canvas.onmousedown = function (e) {
        self.paintTypeArr["painting"] = true;

        let x1 = e.clientX;
        let y1 = e.clientY;
        x1 -= rect.left;
        y1 -= rect.top;
        lastPoint = { x: x1 * scaleX, y: y1 * scaleY };

        if (self.paintTypeArr["text"]) {
          let textarea = document.getElementById("textarea");
          if (self.isShowText) {
            let textContent = textarea.value;
            self.isShowText = false;
            textarea.value = "";
            self.drawText(textPoint.x, textPoint.y, textContent);
          } else if (!self.isShowText) {
            self.isShowText = true;
            textarea.style.left = lastPoint.x + "px";
            textarea.style.top = lastPoint.y + "px";
            textPoint = { x: lastPoint.x, y: lastPoint.y };
            // textarea.style['z-index'] = 6
          }
        }

        if (self.paintTypeArr["erase"]) {
          try {
            this.temp = this.canvas.strokeStyle;
          } catch (error) {
            console.log();
          }
        }

        var thee = e ? e : window.event;
        self.stopBubble(thee);
      };
      self.canvas.onmousemove = function (e) {
        let x2 = e.clientX;
        let y2 = e.clientY;
        x2 -= rect.left;
        y2 -= rect.top;
        let newPoint = { x: x2 * scaleX, y: y2 * scaleY };

        if (self.isPainting()) {
          self.drawLine(lastPoint.x, lastPoint.y, newPoint.x, newPoint.y);
          lastPoint = newPoint;
        } else if (self.paintTypeArr["erase"]) {
          if (!lastPoint.x && !lastPoint.y) {
            return;
          }
          self.handleErase(lastPoint.x, lastPoint.y, newPoint.x, newPoint.y);
          lastPoint = newPoint;
        } else if (self.paintTypeArr["line"]) {
          self.clearCanvas();
          self.imageData && self.context.putImageData(self.imageData, 0, 0);
          self.drawLine(lastPoint.x, lastPoint.y, newPoint.x, newPoint.y);
        } else if (self.paintTypeArr["arrows"]) {
          self.clearCanvas();
          self.imageData && self.context.putImageData(self.imageData, 0, 0);
          self.drawArrow(lastPoint.x, lastPoint.y, newPoint.x, newPoint.y);
        } else if (self.paintTypeArr["rect"]) {
          self.clearCanvas();
          self.imageData && self.context.putImageData(self.imageData, 0, 0);
          self.drawRect(lastPoint.x, lastPoint.y, newPoint.x, newPoint.y);
        } else if (self.paintTypeArr["circle"]) {
          self.clearCanvas();
          self.imageData && self.context.putImageData(self.imageData, 0, 0);
          self.drawCircle(lastPoint.x, lastPoint.y, newPoint.x, newPoint.y);
        }

        var thee = e ? e : window.event;
        self.stopBubble(thee);
      };
      self.canvas.onmouseup = function () {
        lastPoint = { x: undefined, y: undefined };
        self.canvasDraw();
        self.handleSend(self.historyImageData);

        console.log(123);
        self.filterObject();
      };
    },
    //更新绘画类型数组paintTypeArr的状态
    filterObject(type) {
      if (type !== "erase") this.context.strokeStyle = this.temp;

      if (!type) {
        for (const key in this.paintTypeArr) {
          this.paintTypeArr[key] = false;
        }
      } else {
        for (const key in this.paintTypeArr) {
          key === type
            ? (this.paintTypeArr[key] = true)
            : (this.paintTypeArr[key] = false);
        }
      }
    },
    //阻止事件冒泡
    stopBubble(evt) {
      if (evt.stopPropagation) {
        evt.stopPropagation();
      } else {
        //ie
        evt.cancelBubble = true;
      }
    },
    //判断是否是自由绘画模式
    isPainting() {
      for (let key in this.paintTypeArr) {
        if (key !== "painting" && this.paintTypeArr[key]) {
          return false;
        }
      }
      if (this.paintTypeArr["painting"]) {
        return true;
      }
      return false;
    },
    //橡皮擦
    handleErase(fromX, fromY, toX, toY) {
      let ctx = this.context;
      ctx.beginPath();

      ctx.lineWidth = this.lwidth * 10;

      if (this.flag) {
        this.temp = this.context.strokeStyle;
        console.log(this.temp);
        this.flag = false;
      }
      this.context.strokeStyle = "white";
      ctx.lineCap = "round";
      ctx.lineJoin = "round";
      ctx.moveTo(fromX, fromY);
      ctx.lineTo(toX, toY);
      ctx.stroke();
      ctx.closePath();
    },
    //画线
    drawLine(fromX, fromY, toX, toY) {
      let ctx = this.context;
      ctx.beginPath();
      ctx.lineWidth = this.lwidth;
      ctx.lineCap = "round";
      ctx.lineJoin = "round";
      ctx.moveTo(fromX, fromY);
      ctx.lineTo(toX, toY);
      ctx.stroke();
      ctx.closePath();
    },
    //画箭头
    drawArrow(fromX, fromY, toX, toY) {
      let ctx = this.context;
      var headlen = 10; //自定义箭头线的长度
      var theta = 45; //自定义箭头线与直线的夹角，个人觉得45°刚刚好
      var arrowX, arrowY; //箭头线终点坐标
      // 计算各角度和对应的箭头终点坐标
      var angle = (Math.atan2(fromY - toY, fromX - toX) * 180) / Math.PI;
      var angle1 = ((angle + theta) * Math.PI) / 180;
      var angle2 = ((angle - theta) * Math.PI) / 180;
      var topX = headlen * Math.cos(angle1);
      var topY = headlen * Math.sin(angle1);
      var botX = headlen * Math.cos(angle2);
      var botY = headlen * Math.sin(angle2);
      ctx.beginPath();
      //画直线
      ctx.moveTo(fromX, fromY);
      ctx.lineTo(toX, toY);

      arrowX = toX + topX;
      arrowY = toY + topY;
      //画上边箭头线
      ctx.moveTo(arrowX, arrowY);
      ctx.lineTo(toX, toY);

      arrowX = toX + botX;
      arrowY = toY + botY;
      //画下边箭头线
      ctx.lineTo(arrowX, arrowY);

      ctx.stroke();
      ctx.closePath();
    },
    //绘制矩形
    drawRect(topLeftX, topLeftY, botRightX, botRightY) {
      let ctx = this.context;
      ctx.strokeRect(
        topLeftX,
        topLeftY,
        Math.abs(botRightX - topLeftX),
        Math.abs(botRightY - topLeftY)
      );
    },
    //画圆
    drawCircle(circleX, circleY, endX, endY) {
      let ctx = this.context;
      let radius = Math.sqrt(
        (circleX - endX) * (circleX - endX) +
          (circleY - endY) * (circleY - endY)
      );
      ctx.beginPath();
      ctx.arc(circleX, circleY, radius, 0, Math.PI * 2, true);
      ctx.stroke();
    },
    //画文字
    drawText(startX, startY, content) {
      let ctx = this.context;
      ctx.save();
      ctx.beginPath();
      ctx.font = "25px orbitron";
      ctx.textBaseline = "top";
      ctx.fillText(content, parseInt(startX), parseInt(startY));
      ctx.restore();
      ctx.closePath();
    },
    //清屏
    clearCanvas() {
      this.context.clearRect(0, 0, this.canvas.width, this.canvas.height);
    },
    //定格画布图片
    canvasDraw() {
      this.imageData = this.context.getImageData(
        0,
        0,
        this.canvas.width,
        this.canvas.height
      );
      this.dataimg = this.canvas.toDataURL();
      this.historyImageData.push(this.dataimg);
      console.log(typeof(this.dataimg)==='string')
    },
    //撤销
    redo() {
      let historyImageData = this.historyImageData;
      let newHistoryImageData = this.newHistoryImageData;
      if (historyImageData.length > 0) {
        let hisImg = historyImageData.pop();
        this.handleSend(this.historyImageData);

        newHistoryImageData.push(hisImg);
        if (historyImageData.length === 0) {
          this.imageData = null;
          this.clearCanvas();
        } else {
          this.redraw(historyImageData[historyImageData.length - 1]);
          console.log("11111111");
        }
      }
    },
    //反撤销
    cancelRedo() {
      if (this.newHistoryImageData.length > 0) {
        const newHisImg = this.newHistoryImageData.pop();
        this.imageData = newHisImg;
        this.redraw(newHisImg);
        this.historyImageData.push(newHisImg);
        this.handleSend(this.historyImageData);
      }
    },
    //保存图片
    downLoad() {
      const imgUrl = this.canvas.toDataURL("image/png");
      const a = document.createElement("a");
      a.href = imgUrl;
      a.download = "绘图保存记录" + new Date().getTime();
      a.target = "_blank";
      document.body.appendChild(a);
      a.click();
    },
    redraw(imgobj) {
      this.clearCanvas();
      // console.log(imgobj);
      let img = new Image();
      img.src = imgobj;
      img.onload = () => {
        this.context.drawImage(img, 0, 0);
      };
    },
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.container {
  width: 100%;
  height: 100%;
  margin: 10px auto;
  overflow: hidden;
}
.tool-container {
  position: fixed;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  border: 2px solid orange;
  border-radius: 10px;
  display: flex;
  justify-content: center;
}
.drawPane {
  padding: 25px 20px;
  height: 120px;
  position: absolute;
  top: -120px;
  left: 0px;
  border-radius: 5px;
  border: 2px solid orangered;
}
.close-draw-pane {
  position: absolute;
  right: 5px;
  top: 5px;
}
.icon-div {
  margin: 10px 12px;
}
.icon :hover {
  cursor: pointer;
}
input[type="range"] {
  -webkit-appearance: none;
  width: 180px;
  height: 24px;
  outline: none;
  margin-bottom: 3px;
}
input[type="range"]::-webkit-slider-runnable-track {
  background-color: orangered;
  height: 4px;
  border-radius: 5px;
}
input[type="range"]::-webkit-slider-thumb {
  -webkit-appearance: none;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: orange;
  cursor: pointer;
  margin-top: -4px;
}
.text-style {
  float: left;
  position: absolute;
  font: 25px orbitron;
  word-break: break-all;
  background-color: transparent;
}
</style>
