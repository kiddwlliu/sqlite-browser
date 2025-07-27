import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

function DatabaseList() {
  const [databases, setDatabases] = useState<string[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetch("/api/databases")
      .then((res) => {
        if (!res.ok) throw new Error("Failed to fetch databases");
        return res.json();
      })
      .then((data) => setDatabases(data))
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div>Loading databases...</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <div className="p-4">
      <h1 className="text-xl font-bold mb-4">Available Databases</h1>
      <ul className="list-disc pl-5">
        {databases.map((db) => (
          <li key={db}>
            <Link className="text-blue-600 hover:underline" to={`/databases/${db}`}>{db}</Link>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default DatabaseList;