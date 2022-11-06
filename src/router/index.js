// 该文件专门用于创建整个应用的路由器
import VueRouter from 'vue-router'
//引入组件
import OpenOrJoin from '../components/OpenOrJoin'
import OpenBoard from '../components/OpenBoard'
import JoinBoard from '../components/JoinBoard'
import Login from '../components/Login'
import DrawBoard from '../components/DrawBoard'
// import DrawBoard from '../pages/DrawBoard'
// import Board from '../Board'

//创建并暴露一个路由器
export default new VueRouter({
    routes: [
        {
			path:'/',
			redirect:'/login'
		},
        {
            name: 'login',
            path: '/login',
            component: Login,
        },
        {
            name: 'openorjoin',
            path: '/openorjoin',
            component: OpenOrJoin,
        }, {
            name: 'open',
            path: '/open',
            component: OpenBoard
        }, {
            name: 'join',
            path: '/join',
            component: JoinBoard
        },
        {
            name:'drawingboard',
            path:'/drawing',
            component:DrawBoard
            
        }
        // {
        //     name: 'board',
        //     path: '/board',
        //     component: Board
        // }

    ]
})
