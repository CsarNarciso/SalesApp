import { useState } from 'react';
import './App.css'

function App() {

  const [responseStatus, setResponseStatus] = useState<number>();
  const [responseBody, setResponseBody] = useState<string>();

  const defaultUrl = "http://localhost:9000"; // gateway url



  const login = async () => {

    const user = {
      username:"user",
      password:"password"
    }

    const response = await fetch(`${defaultUrl}/auth/login`, 
      {
        method: 'POST',
        credentials: 'include',
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(user)
      });

      const data = await response.json();
      setResponseStatus(response.status);
      setResponseBody(JSON.stringify(data));
  }


  const fetchProducts = async () => {

    const response = await fetch(`${defaultUrl}/products`, {method: 'GET'});

    const data = await response.json();
    setResponseStatus(response.status);
    setResponseBody(JSON.stringify(data));
  }


  const fetchUsers = async () => {

      const response = await fetch(`${defaultUrl}/users`, 
      {
        method: 'GET',
        credentials: 'include'
      });
      
      const data = await response.json();
      setResponseStatus(response.status);
      setResponseBody(JSON.stringify(data));
  }



  return (
    <>
      <button onClick={login}>Login</button>
      <button onClick={() => console.log("Refresh!")}>Refresh</button>
      <button onClick={fetchUsers}>LIst users</button>
      <button onClick={fetchProducts}>List Products</button>

      <h1>Response status code</h1>
      <div>{responseStatus}</div>
      <h1>Response body</h1>
      <div>{responseBody}</div>
    </>
  )
}

export default App;