import React , { useState }   from 'react'
import {useNavigate} from "react-router-dom";
export default function  Problemlistpage(){
  const navigate = useNavigate();
  const [searchTerm, setSearchTerm] = useState("");
 

    const problems = [
    {
      id: 1,
      title: "Two Sum",
      difficulty: "Easy",
      tags: ["Array", "HashMap"],
      solved: true,
    },
    {
      id: 2,
      title: "Longest Substring Without Repeating Characters",
      difficulty: "Medium",
      tags: ["String", "Sliding Window"],
      solved: false,
    },
    {
      id: 3,
      title: "Median of Two Sorted Arrays",
      difficulty: "Hard",
      tags: ["Binary Search"],
      solved: false,
    },
  ];  

  const getDifficultyStyle = (level) => {
  if (level === "Easy") return "text-green-400";
  if (level === "Medium") return "text-yellow-400";
  if (level === "Hard") return "text-red-400";
};
{/* Filterlogic */}
 const filteredProblems = problems.filter((p) =>
    p.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
    p.tags.some(tag =>
      tag.toLowerCase().includes(searchTerm.toLowerCase())
    )
  );






  return (
     <div className="min-h-screen bg-black text-gray-200 px-6 py-8">

      {/* Header */}
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-semibold">DSA Problems</h1>

        {/* Search */}
        <input
          type="text"
          placeholder="Search..."
           value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          className="px-4 py-2 bg-[#111827] border border-gray-700 rounded-md focus:outline-none text-sm"
        />
      </div>

      {/* Table Header */}
      <div className="grid grid-cols-12 px-4 py-2 text-gray-400 text-sm border-b border-gray-700">
        <div className="col-span-1">Status</div>
        <div className="col-span-6">Title</div>
        <div className="col-span-3">Tags</div>
        <div className="col-span-2 text-right">Difficulty</div>
      </div>







        {/* Problem Rows */}
<div>

  {filteredProblems.map((p, index) => (

    <div
      key={p.id}
      onClick={() => navigate(`/problems/${p.id}`)}
      className="grid grid-cols-12 px-4 py-3 items-center cursor-pointer border-b border-gray-800 hover:bg-[#1f2937] transition"
    >

      {/* Status */}
      <div className="col-span-1">
        {
          p.solved
            ? <span className="text-green-400">✔</span>
            : <span className="text-gray-600">•</span>
        }
      </div>

      {/* Title */}
      <div className="col-span-6 font-medium">
        {index + 1}. {p.title}
      </div>

      {/* Tags */}
      <div className="col-span-3 flex gap-2 flex-wrap">
        
        {p.tags.map((tag, i) => (

          <span
            key={i}
            className="text-xs bg-[#111827] px-2 py-1 rounded-md border border-gray-700"
          >
            {tag}
          </span>

        ))}

      </div>

      {/* Difficulty */}
      <div
        className={`col-span-2 text-right font-medium ${getDifficultyStyle(p.difficulty)}`}
      >
        {p.difficulty}
      </div>

    </div>

  ))}

</div>
        







</div>

  )



}