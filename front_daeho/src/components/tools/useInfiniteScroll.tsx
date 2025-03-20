import { useEffect, useRef } from "react";

export function useInfiniteScroll(
  callback: () => void,
  hasMore: boolean,
  loading: boolean
) {
  const observerRef = useRef<HTMLDivElement | null>(null); // Ref for the loader element

  useEffect(() => {
    const options = {
      root: null,
      rootMargin: "20px",
      threshold: 1.0,
    };

    const observer = new IntersectionObserver((entries) => {
      const [entry] = entries;
      if (entry.isIntersecting && hasMore && !loading) {
        callback(); // Trigger the callback to load more data
      }
    }, options);

    if (observerRef.current) {
      observer.observe(observerRef.current);
    }

    return () => {
      if (observerRef.current) {
        observer.unobserve(observerRef.current);
      }
    };
  }, [callback, hasMore, loading]);

  return observerRef;
}
