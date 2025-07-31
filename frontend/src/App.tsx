// import { Routes, Route, Navigate } from "react-router-dom"
// import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import DatabaseList from "./components/DatabaseList"
import TableList from "./components/TableList"
import TableViewer from "./components/TableViewer"

// const build = __VITE_BUILD__;
declare const __GLOBAL_URL_BASE__: string;

function App() {
  return (
		<BrowserRouter basename={__GLOBAL_URL_BASE__}>
      <Routes>
        <Route path="" element={<DatabaseList />} />
        <Route path="/" element={<DatabaseList />} />
				<Route path="index.html" element={<DatabaseList />} />
        <Route path="/databases" element={<DatabaseList />} />
        <Route path="/databases/:db" element={<TableList />} />
        <Route path="/databases/:db/:table" element={<TableViewer />} />
      </Routes>
		</BrowserRouter>
  )
}

export default App;
