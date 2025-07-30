
import { useParams } from "react-router-dom";
import { useEffect, useState } from "react";

function TableViewer() {
  const { db, table } = useParams();
  const [columns, setColumns] = useState<any[]>([]);
  const [rows, setRows] = useState<any[]>([]);
  const [search, setSearch] = useState("");
  const [currentPage, setCurrentPage] = useState(1);
  const rowsPerPage = 20;
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!db || !table) return;
    Promise.all([
      fetch(`/api/databases/${db}/tables/${table}/columns`).then((res) => {
        if (!res.ok) throw new Error("Failed to fetch columns");
        return res.json();
      }),
      fetch(`/api/databases/${db}/tables/${table}`).then((res) => {
        if (!res.ok) throw new Error("Failed to fetch table content");
        return res.json();
      })
    ])
      .then(([cols, data]) => {
        setColumns(cols);
        setRows(data);
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, [db, table]);

  const filteredRows = rows.filter((row) =>
    Object.values(row).some((value) =>
      value?.toString().toLowerCase().includes(search.toLowerCase())
    )
  );

  const paginatedRows = filteredRows.slice((currentPage - 1) * rowsPerPage, currentPage * rowsPerPage);
  const totalPages = Math.ceil(filteredRows.length / rowsPerPage);

  const exportCSV = () => {
    const csvContent = [
      columns.map((col) => col.name).join(","),
      ...filteredRows.map((row) =>
        columns.map((col) => JSON.stringify(row[col.name] ?? "")).join(",")
      )
    ].join("\n");

    const blob = new Blob([csvContent], { type: "text/csv;charset=utf-8;" });
    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.setAttribute("href", url);
    link.setAttribute("download", `${table}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  if (loading) return <div>Loading table...</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <div className="p-4 overflow-auto">
      <h1 className="text-xl font-bold mb-4">{table} in {db}</h1>

      <div className="flex items-center gap-4 mb-4">
        <input
          type="text"
          className="border rounded px-2 py-1"
          placeholder="Search..."
          value={search}
          onChange={(e) => {
            setSearch(e.target.value);
            setCurrentPage(1);
          }}
        />
        <button className="bg-blue-600 text-white px-3 py-1 rounded" onClick={exportCSV}>Export CSV</button>
      </div>

      <table className="table-auto border border-collapse w-full">
        <thead>
          <tr>
            {columns.map((col) => (
              <th key={col.name} className="border px-2 py-1 bg-gray-100 text-left">{col.name}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {paginatedRows.map((row, idx) => (
            <tr key={idx}>
              {columns.map((col) => (
                <td key={col.name} className="border px-2 py-1">{row[col.name]}</td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>

      <div className="mt-4 flex justify-between items-center">
        <span>
          Page {currentPage} of {totalPages}
        </span>
        <div className="flex gap-2">
          <button
            className="px-3 py-1 border rounded disabled:opacity-50"
            disabled={currentPage === 1}
            onClick={() => setCurrentPage((prev) => Math.max(prev - 1, 1))}
          >
            Previous
          </button>
          <button
            className="px-3 py-1 border rounded disabled:opacity-50"
            disabled={currentPage === totalPages}
            onClick={() => setCurrentPage((prev) => Math.min(prev + 1, totalPages))}
          >
            Next
          </button>
        </div>
      </div>
    </div>
  );
}

export default TableViewer;
