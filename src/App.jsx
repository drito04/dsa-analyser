import React from 'react'
import { BrowserRouter,Routes,Route} from 'react-router-dom';
import Home from './pages/Home';
import Profile from './pages/Profile';
import Signin from './pages/Signin';
import Signup from './pages/Signup';
import Problemlistpage from './pages/Problemlistpage';
import Problemdetailpage from './pages/Problemdetailpage';


export default function App() {
  return<BrowserRouter>
  <Routes>
    <Route path = "/" element = {<Home/>}/>
    <Route path = "/profile" element = {<Profile/>}/>
    <Route path = "/signin" element = {<Signin/>}/>
    <Route path = "/signup" element = {<Signup/>}/>
    <Route path = "/problemlist" element = {<Problemlistpage/>}/>
    <Route path = "/problemdetail" element = {<Problemdetailpage/>}/>

  </Routes>

  </BrowserRouter>
  }
  

