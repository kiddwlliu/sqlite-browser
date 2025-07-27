
import { useParams, Link } from "react-router-dom";
import { useEffect, useState } from "react";

function TableList() {
  const { db } = useParams();
  const [tables, setTables] = useState<string[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!db) return;
    fetch(`/api/databases/${db}/tables`)
      .then((res) => {
        if (!res.ok) throw new Error("Failed to fetch tables");
        return res.json();
      })
      .then((data) => setTables(data))
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, [db]);

  if (loading) return <div>Loading tables...</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <div className="p-4">
      <h1 className="text-xl font-bold mb-4">Tables in {db}</h1>
      <ul className="list-disc pl-5">
        {tables.map((table) => (
          <li key={table}>
            <Link className="text-blue-600 hover:underline" to={`/databases/${db}/${table}`}>{table}</Link>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default TableList;