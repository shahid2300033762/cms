import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Navbar from '@/components/Navbar';
import Footer from '@/components/Footer';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { api } from '@/lib/api';

const Dashboard: React.FC = () => {
  const [postsCount, setPostsCount] = useState<number | null>(null);
  const [commentsCount, setCommentsCount] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let isMounted = true;
    (async () => {
      try {
        const [posts, comments] = await Promise.all([
          api.get('/posts'),
          api.get('/comments'),
        ]);
        if (!isMounted) return;
        setPostsCount(Array.isArray(posts) ? posts.length : 0);
        setCommentsCount(Array.isArray(comments) ? comments.length : 0);
      } catch {
        if (isMounted) {
          setPostsCount(null);
          setCommentsCount(null);
        }
      } finally {
        if (isMounted) setLoading(false);
      }
    })();
    return () => { isMounted = false; };
  }, []);

  return (
    <div className="min-h-screen flex flex-col bg-gradient-to-b from-gray-50 to-white">
      <Navbar />
      <main className="flex-1 container mx-auto px-4 py-10 md:py-14">
        <div className="mb-8 flex items-center justify-between">
          <div>
            <h1 className="text-3xl md:text-4xl font-serif font-bold">Dashboard</h1>
            <p className="text-navy/70 mt-2">Quick overview and shortcuts</p>
          </div>
          <div className="flex gap-3">
            <Button asChild className="bg-teal hover:bg-teal/90 text-white">
              <Link to="/write">Write Article</Link>
            </Button>
            <Button asChild variant="outline">
              <Link to="/posts">Browse Posts</Link>
            </Button>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-10">
          <Card>
            <CardHeader>
              <CardTitle>Posts</CardTitle>
            </CardHeader>
            <CardContent>
              <p className="text-4xl font-bold">{loading ? '—' : postsCount ?? '—'}</p>
              <p className="text-navy/60 mt-1">Total published posts</p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Comments</CardTitle>
            </CardHeader>
            <CardContent>
              <p className="text-4xl font-bold">{loading ? '—' : commentsCount ?? '—'}</p>
              <p className="text-navy/60 mt-1">Total comments</p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Getting Started</CardTitle>
            </CardHeader>
            <CardContent className="space-y-3">
              <Button asChild className="w-full bg-teal hover:bg-teal/90 text-white">
                <Link to="/write">Create your first post</Link>
              </Button>
              <Button asChild variant="outline" className="w-full">
                <Link to="/posts">View and manage posts</Link>
              </Button>
            </CardContent>
          </Card>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <Card>
            <CardHeader>
              <CardTitle>Recent Activity</CardTitle>
            </CardHeader>
            <CardContent>
              <p className="text-navy/70">Activity feed coming soon.</p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Tips</CardTitle>
            </CardHeader>
            <CardContent>
              <ul className="list-disc pl-5 text-navy/70 space-y-2">
                <li>Use the Write page to publish articles.</li>
                <li>Open Posts to browse and edit content.</li>
                <li>Tags help readers discover topics.</li>
              </ul>
            </CardContent>
          </Card>
        </div>
      </main>
      <Footer />
    </div>
  );
};

export default Dashboard;


