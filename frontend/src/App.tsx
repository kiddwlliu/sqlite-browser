import { Routes, Route, Navigate } from "react-router-dom"
import DatabaseList from "./components/DatabaseList"
import TableList from "./components/TableList"
import TableViewer from "./components/TableViewer"

function App() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/databases" />} />
      <Route path="/databases" element={<DatabaseList />} />
      <Route path="/databases/:db" element={<TableList />} />
      <Route path="/databases/:db/:table" element={<TableViewer />} />
    </Routes>
  )
}

export default App;
